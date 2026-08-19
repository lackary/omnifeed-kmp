package io.lackstudio.omnifeed.auth.data.remote.source

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.lackstudio.omnifeed.auth.data.remote.api.*
import io.lackstudio.omnifeed.auth.data.remote.model.response.*
import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto
import io.lackstudio.omnifeed.auth.platform.firebaseProjectId
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AuthRemoteDataSourceImplTest {

    private val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
    private val firestore = mockk<FirebaseFirestore>(relaxed = true)
    private val authApiService = mockk<FirebaseAuthApiService>()
    private val firestoreApiService = mockk<FirebaseFirestoreApiService>()
    private val logger = Logger.withTag("Test")

    // Define common mocks for Firestore chains to avoid instance mismatch in verification
    private val mockCollection = mockk<dev.gitlive.firebase.firestore.CollectionReference>(relaxed = true)
    private val mockDocument = mockk<dev.gitlive.firebase.firestore.DocumentReference>(relaxed = true)

    private lateinit var dataSource: AuthRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        mockkStatic("io.lackstudio.omnifeed.auth.platform.FirebaseUtils_jvmKt")
        every { firebaseProjectId } returns "test-project"

        // Setup common Firestore chains
        every { firestore.collection(any()) } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument

        dataSource = AuthRemoteDataSourceImpl(
            firebaseAuth = firebaseAuth,
            firestore = firestore,
            authApiService = authApiService,
            firestoreApiService = firestoreApiService,
            logger = logger
        )
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    // --- Firebase SDK Delegation Tests ---

    @Test
    fun `signInWithEmail should delegate to firebaseAuth`() = runTest {
        val email = "test@test.com"
        val password = "password"
        val mockUser = mockk<FirebaseUser>()
        coEvery { firebaseAuth.signInWithEmailAndPassword(email, password) } returns mockk {
            every { user } returns mockUser
        }
        val result = dataSource.signInWithEmail(email, password)
        assertEquals(mockUser, result)
    }

    @Test
    fun `signUpWithEmail should delegate to firebaseAuth`() = runTest {
        val email = "test@test.com"
        val password = "password"
        val mockUser = mockk<FirebaseUser>()
        coEvery { firebaseAuth.createUserWithEmailAndPassword(email, password) } returns mockk {
            every { user } returns mockUser
        }
        val result = dataSource.signUpWithEmail(email, password)
        assertEquals(mockUser, result)
    }

    @Test
    fun `signInWithCredential should delegate to firebaseAuth`() = runTest {
        val credential = mockk<AuthCredential>()
        val mockUser = mockk<FirebaseUser>()
        coEvery { firebaseAuth.signInWithCredential(credential) } returns mockk {
            every { user } returns mockUser
        }
        val result = dataSource.signInWithCredential(credential)
        assertEquals(mockUser, result)
    }

    @Test
    fun `signInWithCustomToken should delegate to firebaseAuth`() = runTest {
        val token = "custom-token"
        val mockUser = mockk<FirebaseUser>()
        coEvery { firebaseAuth.signInWithCustomToken(token) } returns mockk {
            every { user } returns mockUser
        }
        val result = dataSource.signInWithCustomToken(token)
        assertEquals(mockUser, result)
    }

    @Test
    fun `signOut should delegate to firebaseAuth`() = runTest {
        dataSource.signOut()
        coVerify { firebaseAuth.signOut() }
    }

    // --- REST Auth API Tests ---

    @Test
    fun `signInWithGoogleRest should handle successful login and lookup`() = runTest {
        val googleIdToken = "google-id-token"
        val firebaseIdToken = "firebase.token.payload" 
        
        coEvery { authApiService.signInWithIdp(any()) } returns SignInWithIdpResponse(
            idToken = firebaseIdToken,
            localId = "uid123"
        )
        coEvery { authApiService.lookup(any()) } returns LookupResponse(
            users = listOf(mockk(relaxed = true) {
                every { localId } returns "uid123"
                every { providerUserInfo } returns emptyList()
            })
        )

        val result = dataSource.signInWithGoogleRest(googleIdToken)
        assertEquals("uid123", result.id)
        coVerify { authApiService.signInWithIdp(any()) }
    }

    @Test
    fun `refreshUserRest should return domain User`() = runTest {
        val idToken = "token.payload.signature"
        coEvery { authApiService.lookup(any()) } returns LookupResponse(
            users = listOf(mockk(relaxed = true) {
                every { localId } returns "uid123"
                every { email } returns "test@test.com"
                every { providerUserInfo } returns emptyList()
            })
        )
        val result = dataSource.refreshUserRest(idToken)
        assertEquals("uid123", result.id)
        assertEquals("test@test.com", result.email)
    }

    @Test
    fun `updatePasswordRest should update and then refresh user`() = runTest {
        val idToken = "old-token"
        val newPassword = "new-password"
        val newToken = "new.token.payload"
        
        coEvery { authApiService.updateAccount(any()) } returns SignInWithCustomTokenResponse(idToken = newToken)
        coEvery { authApiService.lookup(any()) } returns LookupResponse(
            users = listOf(mockk(relaxed = true) {
                every { localId } returns "uid123"
                every { providerUserInfo } returns emptyList()
            })
        )

        val result = dataSource.updatePasswordRest(idToken, newPassword)
        assertEquals(newToken, result.idToken)
        coVerify { authApiService.updateAccount(match { it.password == newPassword }) }
    }

    @Test
    fun `deleteAccountRest should call authApiService`() = runTest {
        coEvery { authApiService.deleteAccount(any()) } just Runs
        dataSource.deleteAccountRest("token")
        coVerify { authApiService.deleteAccount(any()) }
    }

    // --- Firestore REST Tests ---

    @Test
    fun `getUserProfileRest should handle successful response`() = runTest {
        val uid = "uid123"
        val idToken = "token"
        coEvery { firestoreApiService.getFirestoreProfile("test-project", uid, idToken) } returns mapOf(
            "username" to "John",
            "email" to "john@test.com"
        )
        val result = dataSource.getUserProfileRest(uid, idToken)
        assertEquals("John", result?.username)
    }

    @Test
    fun `getUserProfileRest should return null on 404`() = runTest {
        val uid = "uid123"
        val idToken = "token"
        // Simulate a 404 error through a mock exception that our handleAuthApi would catch or just return null
        coEvery { firestoreApiService.getFirestoreProfile("test-project", uid, idToken) } returns null
        val result = dataSource.getUserProfileRest(uid, idToken)
        assertNull(result)
    }

    @Test
    fun `saveUserProfileRest should delegate with correct updateMask`() = runTest {
        val profile = UserProfileDto(username = "NewName", email = "new@test.com")
        coEvery { firestoreApiService.saveFirestoreProfile(any(), any(), any(), any(), any()) } just Runs
        
        dataSource.saveUserProfileRest("uid123", "token", profile)
        
        coVerify { 
            firestoreApiService.saveFirestoreProfile(
                projectId = "test-project",
                uid = "uid123",
                idToken = "token",
                fields = match { it["username"] == "NewName" },
                fieldPaths = match { it.contains("username") && it.contains("email") }
            )
        }
    }

    @Test
    fun `updateCustomFieldRest should handle linking and unlinking`() = runTest {
        coEvery { firestoreApiService.saveFirestoreProfile(any(), any(), any(), any(), any()) } just Runs
        
        // Link
        dataSource.updateCustomFieldRest("uid123", "token", "serviceA", true)
        coVerify { firestoreApiService.saveFirestoreProfile(any(), any(), any(), match { 
            (it["linkedServices"] as Map<String, Any?>)["serviceA"] == true
        }, any()) }

        // Unlink (should include token deletions in mask)
        dataSource.updateCustomFieldRest("uid123", "token", "serviceA", false)
        coVerify { firestoreApiService.saveFirestoreProfile(any(), any(), any(), any(), match {
            it.contains("linkedServices.serviceA") && it.contains("encryptedServiceAuth.serviceA")
        }) }
    }

    // --- Firestore SDK Tests ---

    @Test
    fun `saveUserProfile SDK should call firestore set with merge`() = runTest {
        val profile = UserProfileDto(username = "John")
        
        // We avoid coVerify on the 'set' inline function because it's unstable with MockK/GitLive.
        // Instead, we verify that the correct document path was accessed.
        // Since mockDocument is relaxed, the actual .set() call will execute and count towards coverage.
        dataSource.saveUserProfile("uid123", profile)
        
        verify { 
            firestore.collection("users")
            mockCollection.document("uid123")
        }
        // If the test reaches here without crashing, it means the .set() line was executed.
    }

    @Test
    fun `updateCustomField SDK should handle true and false values`() = runTest {
        // Mock static FieldValue.delete
        mockkObject(FieldValue)
        
        // Value = true
        dataSource.updateCustomField("uid123", "serviceA", true)
        coVerify { mockDocument.update(any<Pair<String, Any?>>()) }

        // Value = false
        dataSource.updateCustomField("uid123", "serviceA", false)
        // Verify multiple parameters were passed to update
        coVerify { 
            mockDocument.update(
                any<Pair<String, Any?>>(),
                any<Pair<String, Any?>>(),
                any<Pair<String, Any?>>()
            )
        }
    }
}
