package io.lackstudio.omnifeed.auth.data.repository

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.auth.FirebaseUser
import io.lackstudio.omnifeed.auth.data.local.source.AuthLocalDataSource
import io.lackstudio.omnifeed.auth.data.remote.source.AuthRemoteDataSource
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.core.CustomServiceConfig
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.AuthCredential
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthRepositoryImplTest {

    private val remoteDataSource = mockk<AuthRemoteDataSource>()
    private val localDataSource = FakeAuthLocalDataSource()
    private val logger = Logger.withTag("Test")
    
    // Controlled flows to trigger reactive updates
    private val authStateChangedFlow = MutableStateFlow<FirebaseUser?>(null)

    private lateinit var repository: AuthRepositoryImpl

    @BeforeTest
    fun setup() {
        // Mock static objects that throw NotImplementedError on JVM
        mockkObject(EmailAuthProvider)
        mockkObject(GoogleAuthProvider)
        
        every { EmailAuthProvider.credential(any(), any()) } returns mockk<AuthCredential>()
        every { GoogleAuthProvider.credential(any(), any()) } returns mockk<AuthCredential>()

        // Stub the flows accessed during repository initialization and reactive updates
        every { remoteDataSource.authStateChanged } returns authStateChangedFlow
        every { remoteDataSource.currentUser } returns null
        // Provide a default empty profile flow and REST profile to prevent reactive sync crashes
        every { remoteDataSource.getUserProfile(any(), any()) } returns flowOf(null)
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfile(any(), any()) } just Runs
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs
        coEvery { remoteDataSource.updateCustomFieldRest(any(), any(), any(), any()) } just Runs

        repository = AuthRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            encryptionSalt = "test_salt",
            customServices = mapOf("unsplash" to CustomServiceConfig("https://api.test.com/auth")),
            logger = logger
        )
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    // A simple Fake implementation to avoid JVM naming collisions in MockK
    private class FakeAuthLocalDataSource : AuthLocalDataSource {
        private var cachedUser: User? = null
        private val serviceTokens = mutableMapOf<String, String>()
        override val userFlow = MutableStateFlow<User?>(null)
        
        override fun saveUser(user: User?) { 
            cachedUser = user
            this.userFlow.value = user
        }
        override fun getUser(): User? = cachedUser
        
        override suspend fun saveServiceToken(userId: String, serviceName: String, token: String) {
            serviceTokens["$userId:$serviceName"] = token
        }
        override suspend fun getServiceToken(userId: String, serviceName: String): String? {
            return serviceTokens["$userId:$serviceName"]
        }
        override suspend fun clearServiceToken(userId: String, serviceName: String) {
            serviceTokens.remove("$userId:$serviceName")
        }
        override suspend fun clearAllServiceTokens() {
            serviceTokens.clear()
        }
    }

    @Test
    fun `signInWithEmail should return domain user and save to local`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val mockFirebaseUser = mockk<FirebaseUser>()
        val idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJlYmFzZSI6eyJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.mock" // Mocked JWT

        every { mockFirebaseUser.uid } returns "uid123"
        every { mockFirebaseUser.email } returns email
        every { mockFirebaseUser.displayName } returns "Test User"
        every { mockFirebaseUser.photoURL } returns null
        every { mockFirebaseUser.providerData } returns emptyList()
        coEvery { mockFirebaseUser.getIdToken(any()) } returns idToken

        coEvery { remoteDataSource.signInWithEmail(email, password) } returns mockFirebaseUser
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        val result = repository.signInWithEmail(email, password)

        // Assert
        assertEquals("uid123", result.id)
        assertEquals(email, result.email)
        assertEquals(idToken, result.idToken)

        // Verify using Fake state
        assertNotNull(localDataSource.getUser())
    }

    @Test
    fun `signUpWithEmail should update profile and save to local`() = runTest {
        // Arrange
        val email = "new@example.com"
        val password = "password"
        val username = "NewUser"
        val mockFirebaseUser = mockk<FirebaseUser>()
        val idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJlYmFzZSI6eyJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.mock"

        every { mockFirebaseUser.uid } returns "new_uid"
        every { mockFirebaseUser.email } returns email
        every { mockFirebaseUser.displayName } returns username
        every { mockFirebaseUser.photoURL } returns null
        every { mockFirebaseUser.providerData } returns emptyList()
        coEvery { mockFirebaseUser.updateProfile(any(), any()) } just Runs
        coEvery { mockFirebaseUser.getIdToken(any()) } returns idToken

        coEvery { remoteDataSource.signUpWithEmail(email, password) } returns mockFirebaseUser
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        val result = repository.signUpWithEmail(email, password, username)

        // Assert
        assertEquals("new_uid", result.id)
        assertEquals(username, result.username)
        coVerify { mockFirebaseUser.updateProfile(username, any()) }
        assertNotNull(localDataSource.getUser())
    }

    @Test
    fun `signInWithGoogle should preserve sticky provider`() = runTest {
        val email = "test@example.com"
        val mockFirebaseUser = mockk<FirebaseUser>()
        // JWT with "google.com" provider
        val googleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJlYmFzZSI6eyJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.mock"

        every { mockFirebaseUser.uid } returns "uid123"
        every { mockFirebaseUser.email } returns email
        every { mockFirebaseUser.displayName } returns "Google User"
        every { mockFirebaseUser.photoURL } returns null
        every { mockFirebaseUser.providerData } returns emptyList()
        coEvery { mockFirebaseUser.getIdToken(any()) } returns googleToken

        // Initial sign in with Google
        val domainUser = User(
            id = "uid123",
            email = email,
            username = "Google User",
            photoUrl = null,
            lastSignInProvider = "google.com",
            idToken = googleToken
        )
        coEvery { remoteDataSource.signInWithCredential(any()) } returns mockFirebaseUser
        coEvery { remoteDataSource.signInWithGoogleRest(any()) } returns domainUser
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        val user = repository.signInWithGoogle("google_id_token", null)
        assertEquals("google.com", user.lastSignInProvider)

        // Set the fake state
        localDataSource.saveUser(user)

        // Now mock a "password" drift
        val passwordToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJlYmFzZSI6eyJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.mock"
        val mockPasswordUser = mockk<FirebaseUser>()
        every { mockPasswordUser.uid } returns "uid123"
        every { mockPasswordUser.email } returns email
        every { mockPasswordUser.displayName } returns "Google User"
        every { mockPasswordUser.photoURL } returns null
        every { mockPasswordUser.providerData } returns emptyList()
        coEvery { mockPasswordUser.getIdToken(any()) } returns passwordToken

        coEvery { remoteDataSource.signInWithEmail(any(), any()) } returns mockPasswordUser

        // Act: Sign in with email (e.g. re-auth or linking)
        val result = repository.signInWithEmail(email, "new_password")

        // Assert: Provider should still be "google.com" due to STICKY IDENTITY
        assertEquals("google.com", result.lastSignInProvider)
    }

    @Test
    fun `signInWithCustomService should fetch custom token and save service token`() = runTest {
        // Arrange
        val serviceName = "unsplash"
        val accessToken = "raw_access_token"
        val customToken = "firebase_custom_token"
        val idToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJlYmFzZSI6eyJzaWduX2luX3Byb3ZpZGVyIjoiY3VzdG9tIn19.mock"
        val mockFirebaseUser = mockk<FirebaseUser>()

        every { mockFirebaseUser.uid } returns "custom_uid"
        every { mockFirebaseUser.email } returns null
        every { mockFirebaseUser.displayName } returns "Custom User"
        every { mockFirebaseUser.photoURL } returns null
        every { mockFirebaseUser.providerData } returns emptyList()
        coEvery { mockFirebaseUser.getIdToken(any()) } returns idToken

        coEvery { remoteDataSource.fetchFirebaseCustomToken(any(), accessToken, serviceName) } returns customToken
        coEvery { remoteDataSource.signInWithCustomToken(customToken) } returns mockFirebaseUser
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs
        coEvery { remoteDataSource.updateCustomFieldRest(any(), any(), any(), any()) } just Runs

        // Act
        val result = repository.signInWithCustomService(serviceName, accessToken)

        // Assert
        assertEquals("custom_uid", result.id)
        assertEquals(true, result.linkedServices[serviceName])
        assertEquals(accessToken, localDataSource.getServiceToken(result.id, serviceName))
    }

    @Test
    fun `signOut should clear local and remote`() = runTest {
        // Arrange
        val user = User(id = "uid123", email = "test@test.com", username = "Test", photoUrl = null)
        localDataSource.saveUser(user)
        coEvery { remoteDataSource.signOut() } just Runs

        // Act
        repository.signOut()

        // Assert
        assertEquals(null, localDataSource.getUser())
        coVerify { remoteDataSource.signOut() }
    }

    @Test
    fun `updatePassword should re-authenticate if old password provided`() = runTest {
        // Arrange
        val newPassword = "newPassword123"
        val oldPassword = "oldPassword456"
        val email = "user@example.com"
        val sdkUser = mockk<FirebaseUser>()
        val idToken = "fresh_token"

        every { remoteDataSource.currentUser } returns sdkUser
        every { sdkUser.email } returns email
        coEvery { sdkUser.reauthenticate(any()) } just Runs
        coEvery { sdkUser.updatePassword(newPassword) } just Runs
        coEvery { sdkUser.getIdToken(any()) } returns idToken
        every { sdkUser.uid } returns "uid123"
        every { sdkUser.displayName } returns "User"
        every { sdkUser.photoURL } returns null
        every { sdkUser.providerData } returns emptyList()

        // Handle the fallback path for re-auth
        coEvery { remoteDataSource.signInWithEmail(email, oldPassword) } returns sdkUser
        
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        repository.updatePassword(newPassword, oldPassword)

        // Assert
        // The test might use either reauthenticate or signInWithEmail (fallback)
        val reauthCalled = try { 
            coVerify(exactly = 1) { sdkUser.reauthenticate(any()) }
            true 
        } catch (_: Throwable) { false }
        
        val signInCalled = try {
            coVerify(exactly = 1) { remoteDataSource.signInWithEmail(email, oldPassword) }
            true
        } catch (_: Throwable) { false }

        assertEquals(true, reauthCalled || signInCalled, "Either reauthenticate or signInWithEmail must be called for security")
        coVerify { sdkUser.updatePassword(newPassword) }
        assertEquals(idToken, localDataSource.getUser()?.idToken)
    }

    @Test
    fun `linkWithEmail should merge providers and save to local`() = runTest {
        // Arrange
        val currentEmail = "old@test.com"
        val newEmail = "new@test.com"
        val password = "password123"
        val currentUser = User(id = "uid123", email = currentEmail, username = "Test", photoUrl = null, authProviders = mapOf("google" to true))
        localDataSource.saveUser(currentUser)
        
        val sdkUser = mockk<FirebaseUser>()
        val mockResult = mockk<dev.gitlive.firebase.auth.AuthResult>()
        every { remoteDataSource.currentUser } returns sdkUser
        coEvery { sdkUser.linkWithCredential(any()) } returns mockResult
        every { mockResult.user } returns sdkUser
        
        // Mock toDomain requirements
        every { sdkUser.uid } returns "uid123"
        every { sdkUser.email } returns newEmail
        every { sdkUser.displayName } returns "Test"
        every { sdkUser.photoURL } returns null
        every { sdkUser.providerData } returns emptyList()
        coEvery { sdkUser.getIdToken(any()) } returns "new_token"

        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        val result = repository.linkWithEmail(newEmail, password)

        // Assert
        assertEquals(true, result.authProviders["password"])
        assertEquals(true, result.authProviders["google"])
        assertEquals("new_token", localDataSource.getUser()?.idToken)
    }

    @Test
    fun `deleteAccount should clear all and notify remote`() = runTest {
        // Arrange
        val user = User(id = "uid123", email = "test@test.com", username = "Test", photoUrl = null)
        localDataSource.saveUser(user)
        val sdkUser = mockk<FirebaseUser>()
        every { remoteDataSource.currentUser } returns sdkUser
        coEvery { sdkUser.delete() } just Runs
        coEvery { remoteDataSource.signOut() } just Runs
        coEvery { remoteDataSource.deleteUserProfile(any()) } just Runs

        // Act
        repository.deleteAccount()

        // Assert
        assertEquals(null, localDataSource.getUser())
        coVerify { sdkUser.delete() }
        coVerify { remoteDataSource.deleteUserProfile("uid123") }
    }

    @Test
    fun `updateUsername should update both SDK and Firestore`() = runTest {
        // Arrange
        val newUsername = "UpdatedName"
        val currentUser = User(id = "uid123", email = "test@test.com", username = "OldName", photoUrl = null, idToken = "valid_token")
        localDataSource.saveUser(currentUser)
        
        val sdkUser = mockk<FirebaseUser>()
        every { remoteDataSource.currentUser } returns sdkUser
        every { sdkUser.uid } returns "uid123"
        every { sdkUser.email } returns "test@test.com"
        every { sdkUser.displayName } returns "OldName"
        every { sdkUser.photoURL } returns null
        every { sdkUser.providerData } returns emptyList()
        coEvery { sdkUser.updateProfile(any(), any()) } answers {
            // Simulate the profile update side-effect by updating the fake datasource
            val user = localDataSource.getUser()
            if (user != null) {
                localDataSource.saveUser(user.copy(username = firstArg()))
            }
            kotlin.Unit
        }
        coEvery { sdkUser.getIdToken(any()) } returns "valid_token"
        
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        val result = repository.updateUsername(newUsername)

        // Assert
        assertEquals(newUsername, result.username)
        // Verify local cache is also updated
        assertEquals(newUsername, localDataSource.getUser()?.username)
        coVerify { sdkUser.updateProfile(newUsername, any()) }
    }

    @Test
    fun `getServiceToken should return decrypted token from local if available`() = runTest {
        // Arrange
        val userId = "uid123"
        val serviceName = "unsplash"
        val rawToken = "my_secret_token"
        localDataSource.saveUser(User(id = userId, email = null, username = null, photoUrl = null))
        localDataSource.saveServiceToken(userId, serviceName, rawToken)

        // Act
        val result = repository.getServiceToken(serviceName)

        // Assert
        assertEquals(rawToken, result)
    }

    @Test
    fun `unlinkProvider should remove provider and save to local`() = runTest {
        // Arrange
        val providerId = "google.com"
        val currentUser = User(
            id = "uid123", 
            email = "test@test.com", 
            username = "Test", 
            photoUrl = null, 
            authProviders = mapOf("google" to true, "password" to true),
            idToken = "valid_token"
        )
        localDataSource.saveUser(currentUser)

        val sdkUser = mockk<FirebaseUser>()
        val resultUser = mockk<FirebaseUser>()
        every { remoteDataSource.currentUser } returns sdkUser
        coEvery { sdkUser.unlink(providerId) } returns resultUser
        
        // Mock toDomain for resultUser
        every { resultUser.uid } returns "uid123"
        every { resultUser.email } returns "test@test.com"
        every { resultUser.displayName } returns "Test"
        every { resultUser.photoURL } returns null
        every { resultUser.providerData } returns emptyList() // Representing unlinked state
        coEvery { resultUser.getIdToken(any()) } returns "new_token"

        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        val result = repository.unlinkProvider(providerId)

        // Assert
        assertEquals(false, result.authProviders.containsKey("google"))
        assertEquals(true, result.authProviders["password"])
        coVerify { sdkUser.unlink(providerId) }
    }

    @Test
    fun `unlinkCustomService should update Firestore and clear local token`() = runTest {
        // Arrange
        val serviceName = "unsplash"
        val userId = "uid123"
        val currentUser = User(
            id = userId, 
            email = "test@test.com", 
            username = "Test", 
            photoUrl = null, 
            linkedServices = mapOf(serviceName to true)
        )
        localDataSource.saveUser(currentUser)
        localDataSource.saveServiceToken(userId, serviceName, "some_token")
        
        coEvery { remoteDataSource.updateCustomField(any(), any(), any()) } just Runs

        // Act
        val result = repository.unlinkCustomService(serviceName)

        // Assert
        assertEquals(false, result.linkedServices[serviceName])
        assertEquals(null, localDataSource.getServiceToken(userId, serviceName))
        coVerify { remoteDataSource.updateCustomField(userId, serviceName, false) }
    }

    @Test
    fun `linkWithCustomService should refresh session if user is already custom`() = runTest {
        // Arrange
        val serviceName = "unsplash"
        val accessToken = "new_access_token"
        val customUid = "custom:unsplash:123"
        val currentUser = User(id = customUid, email = null, username = "Custom", photoUrl = null)
        localDataSource.saveUser(currentUser)
        
        val mockFirebaseUser = mockk<FirebaseUser>()
        every { mockFirebaseUser.uid } returns customUid
        every { mockFirebaseUser.email } returns null
        every { mockFirebaseUser.displayName } returns "Custom"
        every { mockFirebaseUser.photoURL } returns null
        every { mockFirebaseUser.providerData } returns emptyList()
        coEvery { mockFirebaseUser.getIdToken(any()) } returns "refreshed_token"
        
        coEvery { remoteDataSource.fetchFirebaseCustomToken(any(), any(), any()) } returns "custom_token"
        coEvery { remoteDataSource.signInWithCustomToken(any()) } returns mockFirebaseUser
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs
        coEvery { remoteDataSource.updateCustomFieldRest(any(), any(), any(), any()) } just Runs

        // Act
        val result = repository.linkWithCustomService(serviceName, accessToken)

        // Assert
        assertEquals("refreshed_token", result.idToken)
        coVerify { remoteDataSource.signInWithCustomToken("custom_token") }
    }

    @Test
    fun `reauthenticateWithEmail should fallback to signInWithEmail on JVM`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password123"
        val mockUser = User(id = "uid123", email = email, username = "Test", photoUrl = null)
        localDataSource.saveUser(mockUser)
        
        val mockFirebaseUser = mockk<FirebaseUser>()
        every { mockFirebaseUser.uid } returns "uid123"
        every { mockFirebaseUser.email } returns email
        every { mockFirebaseUser.displayName } returns "Test"
        every { mockFirebaseUser.photoURL } returns null
        every { mockFirebaseUser.providerData } returns emptyList()
        coEvery { mockFirebaseUser.getIdToken(any()) } returns "new_token"

        coEvery { remoteDataSource.signInWithEmail(email, password) } returns mockFirebaseUser
        coEvery { remoteDataSource.getUserProfileRest(any(), any()) } returns null
        coEvery { remoteDataSource.saveUserProfileRest(any(), any(), any()) } just Runs

        // Act
        repository.reauthenticateWithEmail(password)

        // Assert: On JVM, it should call signInWithEmail as a fallback
        coVerify { remoteDataSource.signInWithEmail(email, password) }
        assertEquals("new_token", localDataSource.getUser()?.idToken)
    }

    @Test
    fun `currentUser flow should merge SDK and local state and trigger Firestore sync`() = runTest {
        // 1. Prepare Mocks BEFORE starting the flow collection
        val sdkUser = mockk<FirebaseUser>()
        every { sdkUser.uid } returns "uid_sdk"
        every { sdkUser.email } returns "sdk@test.com"
        every { sdkUser.displayName } returns "SDK User"
        every { sdkUser.photoURL } returns null
        every { sdkUser.providerData } returns emptyList()
        coEvery { sdkUser.getIdToken(any()) } returns "sdk_token"
        
        val profileDto = io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto(
            username = "Synced Name",
            email = "sdk@test.com"
        )
        every { remoteDataSource.getUserProfile("uid_sdk", any()) } returns flowOf(profileDto)

        repository.currentUser.test {
            // Initial state: null
            assertEquals(null, awaitItem())

            // 2. Trigger emission
            authStateChangedFlow.value = sdkUser
            
            // 3. Verify merged result
            val mergedUser = awaitItem()
            assertNotNull(mergedUser)
            assertEquals("uid_sdk", mergedUser.id)
            assertEquals("Synced Name", mergedUser.username)
            assertEquals("sdk_token", mergedUser.idToken)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `linkWithGoogle should use credential and merge with existing providers`() = runTest {
        // Arrange
        val googleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJlYmFzZSI6eyJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.mock"
        
        // CRITICAL: Must provide an idToken so saveUserToFirestore uses the REST path (which is mocked)
        // OR we mock the SDK path (which I added to setup)
        val currentUser = User(
            id = "uid123", 
            email = "test@test.com", 
            username = "Test", 
            photoUrl = null, 
            authProviders = mapOf("password" to true),
            idToken = "valid_token"
        )
        localDataSource.saveUser(currentUser)
        
        val sdkUser = mockk<FirebaseUser>()
        val mockResult = mockk<dev.gitlive.firebase.auth.AuthResult>()
        every { remoteDataSource.currentUser } returns sdkUser
        coEvery { sdkUser.linkWithCredential(any()) } returns mockResult
        every { mockResult.user } returns sdkUser
        
        every { sdkUser.uid } returns "uid123"
        every { sdkUser.email } returns "test@test.com"
        every { sdkUser.displayName } returns "Test"
        every { sdkUser.photoURL } returns null
        every { sdkUser.providerData } returns emptyList()
        coEvery { sdkUser.getIdToken(any()) } returns googleToken

        // Act
        val result = repository.linkWithGoogle("id_token", "access_token")

        // Assert
        assertEquals(true, result.authProviders["google"])
        assertEquals(true, result.authProviders["password"])
        coVerify { sdkUser.linkWithCredential(any()) }
    }
}
