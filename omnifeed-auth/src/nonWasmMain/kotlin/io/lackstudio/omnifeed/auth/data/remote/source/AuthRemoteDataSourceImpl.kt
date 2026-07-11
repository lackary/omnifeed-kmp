package io.lackstudio.omnifeed.auth.data.remote.source

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.lackstudio.omnifeed.auth.data.remote.api.*
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto
import io.lackstudio.omnifeed.auth.domain.model.AuthProvider
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.platform.firebaseProjectId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Remote Data Structure (Firestore):
 *
 * /users/{uid} (Document)
 *    ├── displayName: String?
 *    ├── email: String?
 *    ├── photoUrl: String?
 *    ├── authProviders: Map<String, Boolean> (e.g., {"google": true, "firebase": true})
 *    └── linkedServices: Map<String, Boolean> (e.g., {"unsplash": true})
 */
class AuthRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authApiService: FirebaseAuthApiService,
    private val firestoreApiService: FirebaseFirestoreApiService
) : AuthRemoteDataSource {

    override val authStateChanged: Flow<FirebaseUser?> = firebaseAuth.authStateChanged
    override val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
        return result.user ?: throw Exception("Login failed: User is null")
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
        return result.user ?: throw Exception("Registration failed: User is null")
    }

    override suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser {
        val result = firebaseAuth.signInWithCredential(credential)
        return result.user ?: throw Exception("Login with credential failed: User is null")
    }

    override suspend fun signInWithCustomToken(token: String): FirebaseUser {
        val result = firebaseAuth.signInWithCustomToken(token)
        return result.user ?: throw Exception("Login with custom token failed: User is null")
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun fetchFirebaseCustomToken(endpoint: String, customAccessToken: String, provider: String): String {
        return authApiService.fetchFirebaseCustomToken(endpoint, customAccessToken, provider)
    }

    override suspend fun signInWithGoogleRest(idToken: String): User {
        val resultData = authApiService.signInWithIdp(SignInWithIdpRequest(
            postBody = "id_token=$idToken&providerId=${AuthProvider.GOOGLE.firebaseId}"
        ))

        val firebaseIdToken = resultData.idToken ?: throw Exception("REST login failed: idToken is null")
        
        // Consistency Fix: Perform lookup to get the Firebase account's profile name (e.g. "Henry")
        // instead of the Google IDP provided name ("Yu Chan Huang")
        val lookupData = authApiService.lookup(LookupRequest(idToken = firebaseIdToken))
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("REST login failed: No user info found")

        return User(
            id = userInfo.localId,
            email = userInfo.email,
            displayName = userInfo.displayName ?: resultData.displayName,
            photoUrl = userInfo.photoUrl ?: resultData.photoUrl,
            authProviders = mapOf(AuthProvider.GOOGLE.id to true),
            idToken = firebaseIdToken
        )
    }

    override suspend fun signInWithCustomTokenRest(customToken: String, serviceName: String, accessToken: String): User {
        val resultData = authApiService.signInWithCustomToken(SignInWithCustomTokenRequest(token = customToken))
        val firebaseIdToken = resultData.idToken ?: throw Exception("REST login failed: idToken is null")

        val lookupData = authApiService.lookup(LookupRequest(idToken = firebaseIdToken))
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("REST login failed: No user info found")

        return User(
            id = userInfo.localId,
            email = userInfo.email,
            displayName = userInfo.displayName,
            photoUrl = userInfo.photoUrl,
            authProviders = if (userInfo.providerUserInfo.any { it.providerId == AuthProvider.GOOGLE.firebaseId }) {
                mapOf(AuthProvider.GOOGLE.id to true)
            } else emptyMap(),
            linkedServices = mapOf(serviceName to true),
            idToken = firebaseIdToken
        )
    }

    override suspend fun linkWithGoogleRest(idToken: String, currentFirebaseIdToken: String): User {
        val resultData = authApiService.signInWithIdp(SignInWithIdpRequest(
            postBody = "id_token=$idToken&providerId=${AuthProvider.GOOGLE.firebaseId}",
            idToken = currentFirebaseIdToken,
            requestUri = "http://localhost"
        ))

        val firebaseIdToken = resultData.idToken ?: throw Exception("REST linking failed: idToken is null")
        
        // Consistency Fix: Get the Firebase account's profile name
        val lookupData = authApiService.lookup(LookupRequest(idToken = firebaseIdToken))
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("REST linking failed: No user info found")

        return User(
            id = userInfo.localId,
            email = userInfo.email,
            displayName = userInfo.displayName ?: resultData.displayName,
            photoUrl = userInfo.photoUrl ?: resultData.photoUrl,
            authProviders = mapOf(AuthProvider.GOOGLE.id to true),
            idToken = firebaseIdToken
        )
    }

    override suspend fun deleteAccountRest(idToken: String) {
        authApiService.deleteAccount(DeleteAccountRequest(idToken = idToken))
    }

    override fun getUserProfile(uid: String, serviceFields: List<String>): Flow<UserProfileDto?> {
        return firestore.collection("users").document(uid).snapshots().map { snapshot ->
            if (snapshot.exists) {
                UserProfileDto(
                    displayName = try { snapshot.get<String?>("displayName") } catch (e: Exception) { null },
                    email = try { snapshot.get<String?>("email") } catch (e: Exception) { null },
                    photoUrl = try { snapshot.get<String?>("photoUrl") } catch (e: Exception) { null },
                    authProviders = try {
                        snapshot.get<Map<String, Boolean>?>("authProviders") ?: emptyMap()
                    } catch (e: Exception) { emptyMap() },
                    linkedServices = try {
                        snapshot.get<Map<String, Boolean>?>("linkedServices") ?: emptyMap()
                    } catch (e: Exception) { emptyMap() },
                    encryptedServiceTokens = try {
                        snapshot.get<Map<String, String>?>("encryptedServiceTokens") ?: emptyMap()
                    } catch (e: Exception) { emptyMap() }
                )
            } else {
                null
            }
        }
    }

    override suspend fun getUserProfileRest(uid: String, idToken: String): UserProfileDto? {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        val data = firestoreApiService.getFirestoreProfile(projectId, uid, idToken) ?: return null
        
        return UserProfileDto(
            displayName = data["displayName"] as? String,
            email = data["email"] as? String,
            photoUrl = data["photoUrl"] as? String,
            authProviders = (data["authProviders"] as? Map<*, *>)?.map { it.key.toString() to (it.value as? Boolean ?: false) }?.toMap() ?: emptyMap(),
            linkedServices = (data["linkedServices"] as? Map<*, *>)?.map { it.key.toString() to (it.value as? Boolean ?: false) }?.toMap() ?: emptyMap(),
            encryptedServiceTokens = (data["encryptedServiceTokens"] as? Map<*, *>)?.map { it.key.toString() to it.value.toString() }?.toMap() ?: emptyMap()
        )
    }

    override suspend fun saveUserProfile(uid: String, profile: UserProfileDto) {
        val updateMap = mutableMapOf<String, Any?>()
        profile.displayName?.let { updateMap["displayName"] = it }
        profile.email?.let { updateMap["email"] = it }
        profile.photoUrl?.let { updateMap["photoUrl"] = it }
        profile.authProviders?.let { updateMap["authProviders"] = it }
        profile.linkedServices?.let { updateMap["linkedServices"] = it }
        profile.encryptedServiceTokens?.let { updateMap["encryptedServiceTokens"] = it }

        if (updateMap.isNotEmpty()) {
            firestore.collection("users").document(uid).set(updateMap, merge = true)
        }
    }

    override suspend fun saveUserProfileRest(uid: String, idToken: String, profile: UserProfileDto) {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        val fields = mutableMapOf<String, Any?>()
        val updateMask = mutableListOf<String>()

        profile.displayName?.let { fields["displayName"] = it; updateMask.add("displayName") }
        profile.email?.let { fields["email"] = it; updateMask.add("email") }
        profile.photoUrl?.let { fields["photoUrl"] = it; updateMask.add("photoUrl") }
        profile.authProviders?.let { fields["authProviders"] = it; updateMask.add("authProviders") }
        profile.linkedServices?.let { fields["linkedServices"] = it; updateMask.add("linkedServices") }
        profile.encryptedServiceTokens?.let { fields["encryptedServiceTokens"] = it; updateMask.add("encryptedServiceTokens") }

        if (fields.isNotEmpty()) {
            firestoreApiService.saveFirestoreProfile(
                projectId = projectId,
                uid = uid,
                idToken = idToken,
                fields = fields,
                fieldPaths = updateMask // Ensure only specified fields are touched
            )
        }
    }

    override suspend fun updateCustomField(uid: String, field: String, value: Boolean) {
        firestore.collection("users").document(uid).update("linkedServices.$field" to value)
    }

    override suspend fun updateCustomFieldRest(uid: String, idToken: String, field: String, value: Boolean) {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        val fields = mapOf(
            "linkedServices" to mapOf(field to value)
        )
        firestoreApiService.saveFirestoreProfile(
            projectId = projectId,
            uid = uid,
            idToken = idToken,
            fields = fields,
            fieldPaths = listOf("linkedServices.$field")
        )
    }

    override suspend fun deleteUserProfile(uid: String) {
        firestore.collection("users").document(uid).delete()
    }

    override suspend fun deleteUserProfileRest(uid: String, idToken: String) {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        firestoreApiService.deleteFirestoreProfile(projectId, uid, idToken)
    }
}
