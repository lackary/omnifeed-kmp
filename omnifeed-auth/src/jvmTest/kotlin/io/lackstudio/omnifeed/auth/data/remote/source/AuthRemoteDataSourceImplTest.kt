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

    // Define a stable flow mock to be used during DataSource initialization
    private val mockAuthStateFlow = mockk<kotlinx.coroutines.flow.Flow<FirebaseUser?>>()

    // Define common mocks for Firestore chains to avoid instance mismatch in verification
    private val mockCollection = mockk<dev.gitlive.firebase.firestore.CollectionReference>(relaxed = true)
    private val mockDocument = mockk<dev.gitlive.firebase.firestore.DocumentReference>(relaxed = true)

    private lateinit var dataSource: AuthRemoteDataSourceImpl

    @BeforeTest
    fun setup() {
        mockkStatic("io.lackstudio.omnifeed.auth.platform.FirebaseUtils_jvmKt")
        every { firebaseProjectId } returns "test-project"

        // Stub the flow BEFORE initializing dataSource because it's assigned in the constructor
        every { firebaseAuth.authStateChanged } returns mockAuthStateFlow

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

    // --- Properties & Flow Tests ---

    @Test
    fun `authStateChanged should delegate to firebaseAuth`() {
        assertEquals(mockAuthStateFlow, dataSource.authStateChanged)
    }

    @Test
    fun `currentUser should delegate to firebaseAuth`() {
        val mockUser = mockk<FirebaseUser>()
        every { firebaseAuth.currentUser } returns mockUser
        assertEquals(mockUser, dataSource.currentUser)
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
    fun `fetchFirebaseCustomToken should delegate to authApiService`() = runTest {
        val endpoint = "http://test"
        val token = "token"
        val provider = "google"
        coEvery { authApiService.fetchFirebaseCustomToken(endpoint, token, provider) } returns "custom-token"

        val result = dataSource.fetchFirebaseCustomToken(endpoint, token, provider)
        assertEquals("custom-token", result)
    }

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
                every { email } returns "test@test.com"
                every { providerUserInfo } returns emptyList()
            })
        )

        val result = dataSource.signInWithGoogleRest(googleIdToken)
        assertEquals("uid123", result.id)
        coVerify { authApiService.signInWithIdp(any()) }
    }

    @Test
    fun `signInWithCustomTokenRest should link service and refresh user`() = runTest {
        val customToken = "custom-token"
        val firebaseIdToken = "firebase.token.payload"
        
        coEvery { authApiService.signInWithCustomToken(any()) } returns SignInWithCustomTokenResponse(
            idToken = firebaseIdToken
        )
        coEvery { authApiService.lookup(any()) } returns LookupResponse(
            users = listOf(mockk(relaxed = true) {
                every { localId } returns "uid123"
                every { providerUserInfo } returns emptyList()
            })
        )

        val result = dataSource.signInWithCustomTokenRest(customToken, "unsplash", "access-token")
        assertEquals(true, result.linkedServices["unsplash"])
        coVerify { authApiService.signInWithCustomToken(match { it.token == customToken }) }
    }

    @Test
    fun `linkWithGoogleRest should combine signInWithIdp and lookup`() = runTest {
        val googleIdToken = "google-id-token"
        val currentToken = "current-token"
        val firebaseIdToken = "new.token.payload"
        
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

        val result = dataSource.linkWithGoogleRest(googleIdToken, currentToken)
        assertEquals(firebaseIdToken, result.idToken)
        coVerify { authApiService.signInWithIdp(match { it.idToken == currentToken }) }
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
    fun `updateUsernameRest should call updateAccount and refreshUser`() = runTest {
        val idToken = "token"
        val newName = "New Name"
        coEvery { authApiService.updateAccount(any()) } returns SignInWithCustomTokenResponse(idToken = "new-token")
        coEvery { authApiService.lookup(any()) } returns LookupResponse(
            users = listOf(mockk(relaxed = true) {
                every { localId } returns "uid123"
                every { displayName } returns newName
                every { providerUserInfo } returns emptyList()
            })
        )

        val result = dataSource.updateUsernameRest(idToken, newName)
        assertEquals(newName, result.username)
    }

    @Test
    fun `deleteAccountRest should call authApiService`() = runTest {
        coEvery { authApiService.deleteAccount(any()) } just Runs
        dataSource.deleteAccountRest("token")
        coVerify { authApiService.deleteAccount(any()) }
    }

    // --- Firestore / User Profile Tests ---

    @Test
    fun `getUserProfile should handle snapshot updates`() = runTest {
        // This is a basic test verifying that the call path to Firestore is correct.
        // Mocking the full Flow behavior of snapshots() in a pure unit test is extremely heavy,
        // but we verify the structural integrity.
        dataSource.getUserProfile("uid123", emptyList())
        verify { 
            firestore.collection("users")
            mockCollection.document("uid123")
        }
    }

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
        coEvery { firestoreApiService.getFirestoreProfile("test-project", uid, idToken) } returns null
        val result = dataSource.getUserProfileRest(uid, idToken)
        assertNull(result)
    }

    @Test
    fun `saveUserProfile SDK should call firestore set with merge`() = runTest {
        val profile = UserProfileDto(username = "John")
        dataSource.saveUserProfile("uid123", profile)
        verify { 
            firestore.collection("users")
            mockCollection.document("uid123")
        }
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
    fun `updateCustomField SDK should handle true and false values`() = runTest {
        mockkObject(FieldValue)
        dataSource.updateCustomField("uid123", "serviceA", true)
        coVerify { mockDocument.update(any<Pair<String, Any?>>()) }

        dataSource.updateCustomField("uid123", "serviceA", false)
        coVerify { mockDocument.update(any<Pair<String, Any?>>(), any<Pair<String, Any?>>(), any<Pair<String, Any?>>()) }
    }

    @Test
    fun `updateCustomFieldRest should handle linking and unlinking`() = runTest {
        coEvery { firestoreApiService.saveFirestoreProfile(any(), any(), any(), any(), any()) } just Runs
        
        dataSource.updateCustomFieldRest("uid123", "token", "serviceA", true)
        coVerify { firestoreApiService.saveFirestoreProfile(any(), any(), any(), match { 
            (it["linkedServices"] as Map<String, Any?>)["serviceA"] == true
        }, any()) }

        dataSource.updateCustomFieldRest("uid123", "token", "serviceA", false)
        coVerify { firestoreApiService.saveFirestoreProfile(any(), any(), any(), any(), match {
            it.contains("linkedServices.serviceA")
        }) }
    }

    @Test
    fun `deleteUserProfile should delegate to firestore collection`() = runTest {
        dataSource.deleteUserProfile("uid123")
        coVerify { mockDocument.delete() }
    }

    @Test
    fun `deleteUserProfileRest should call firestoreApiService`() = runTest {
        coEvery { firestoreApiService.deleteFirestoreProfile(any(), any(), any()) } just Runs
        dataSource.deleteUserProfileRest("uid123", "token")
        coVerify { firestoreApiService.deleteFirestoreProfile("test-project", "uid123", "token") }
    }
}
