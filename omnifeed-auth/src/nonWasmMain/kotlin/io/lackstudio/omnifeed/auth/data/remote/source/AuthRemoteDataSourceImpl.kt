package io.lackstudio.omnifeed.auth.data.remote.source

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.lackstudio.omnifeed.auth.data.error.AuthApiException
import io.lackstudio.omnifeed.auth.data.remote.api.*
import io.lackstudio.omnifeed.auth.data.remote.model.request.*
import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserProfileDto
import io.lackstudio.omnifeed.auth.domain.model.AuthProvider
import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.platform.firebaseProjectId
import io.lackstudio.omnifeed.core.network.error.RemoteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        return handleAuthApi(name = "fetchFirebaseCustomToken") {
            authApiService.fetchFirebaseCustomToken(endpoint, customAccessToken, provider)
        }
    }

    override suspend fun signInWithGoogleRest(idToken: String): User {
        val resultData = handleAuthApi(name = "signInWithGoogleRest") {
            authApiService.signInWithIdp(SignInWithIdpRequest(
                postBody = "id_token=$idToken&providerId=${AuthProvider.GOOGLE.firebaseId}"
            ))
        }

        val firebaseIdToken = resultData.idToken ?: throw Exception("REST login failed: idToken is null")
        
        val lookupData = handleAuthApi(name = "lookup") {
            authApiService.lookup(LookupRequest(idToken = firebaseIdToken))
        }
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("REST login failed: No user info found")

        val providers = userInfo.providerUserInfo.associate {
            val key = AuthProvider.fromFirebaseId(it.providerId)?.id ?: it.providerId
            key to true
        }

        return User(
            id = userInfo.localId,
            email = userInfo.email,
            username = userInfo.displayName ?: resultData.displayName,
            photoUrl = userInfo.photoUrl ?: resultData.photoUrl,
            authProviders = providers,
            idToken = firebaseIdToken
        )
    }

    override suspend fun signInWithCustomTokenRest(customToken: String, serviceName: String, accessToken: String): User {
        val resultData = handleAuthApi(name = "signInWithCustomTokenRest") {
            authApiService.signInWithCustomToken(SignInWithCustomTokenRequest(token = customToken))
        }
        val firebaseIdToken = resultData.idToken ?: throw Exception("REST login failed: idToken is null")

        return refreshUserRest(firebaseIdToken).copy(
            linkedServices = mapOf(serviceName to true),
            idToken = firebaseIdToken
        )
    }

    override suspend fun refreshUserRest(idToken: String): User {
        val lookupData = handleAuthApi(name = "refreshUser") {
            authApiService.lookup(LookupRequest(idToken = idToken))
        }
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("Refresh failed: No user info found")

        val providers = userInfo.providerUserInfo.associate {
            val key = AuthProvider.fromFirebaseId(it.providerId)?.id ?: it.providerId
            key to true
        }

        return User(
            id = userInfo.localId,
            email = userInfo.email?.takeIf { it.isNotBlank() },
            username = userInfo.displayName?.takeIf { it.isNotBlank() },
            photoUrl = userInfo.photoUrl?.takeIf { it.isNotBlank() },
            authProviders = providers,
            idToken = idToken
        )
    }

    override suspend fun linkWithGoogleRest(idToken: String, currentFirebaseIdToken: String): User {
        val resultData = handleAuthApi(name = "linkWithGoogleRest") {
            authApiService.signInWithIdp(SignInWithIdpRequest(
                postBody = "id_token=$idToken&providerId=${AuthProvider.GOOGLE.firebaseId}",
                idToken = currentFirebaseIdToken,
                requestUri = "http://localhost"
            ))
        }

        val firebaseIdToken = resultData.idToken ?: throw Exception("REST linking failed: idToken is null")
        
        val lookupData = handleAuthApi(name = "lookup") {
            authApiService.lookup(LookupRequest(idToken = firebaseIdToken))
        }
        val userInfo = lookupData.users.firstOrNull() ?: throw Exception("REST linking failed: No user info found")

        val providers = userInfo.providerUserInfo.associate {
            val key = AuthProvider.fromFirebaseId(it.providerId)?.id ?: it.providerId
            key to true
        }

        return User(
            id = userInfo.localId,
            email = userInfo.email,
            username = userInfo.displayName ?: resultData.displayName,
            photoUrl = userInfo.photoUrl ?: resultData.photoUrl,
            authProviders = providers,
            idToken = firebaseIdToken
        )
    }

    override suspend fun deleteAccountRest(idToken: String) {
        handleAuthApi(name = "deleteAccountRest") {
            authApiService.deleteAccount(DeleteAccountRequest(idToken = idToken))
        }
    }

    override suspend fun updatePasswordRest(idToken: String, newPassword: String): User {
        val result = handleAuthApi(name = "updatePasswordRest") {
            authApiService.updateAccount(UpdateAccountRequest(idToken = idToken, password = newPassword))
        }
        val newToken = result.idToken ?: idToken
        return refreshUserRest(newToken)
    }

    override suspend fun updateUsernameRest(idToken: String, username: String): User {
        val result = handleAuthApi(name = "updateUsernameRest") {
            authApiService.updateAccount(UpdateAccountRequest(idToken = idToken, displayName = username))
        }
        val newToken = result.idToken ?: idToken
        return refreshUserRest(newToken)
    }

    override fun getUserProfile(uid: String, serviceFields: List<String>): Flow<UserProfileDto?> {
        return firestore.collection("users").document(uid).snapshots().map { snapshot ->
            if (snapshot.exists) {
                UserProfileDto(
                    username = try { snapshot.get<String?>("username")?.takeIf { it.isNotBlank() } } catch (e: Exception) { null },
                    email = try { snapshot.get<String?>("email")?.takeIf { it.isNotBlank() } } catch (e: Exception) { null },
                    photoUrl = try { snapshot.get<String?>("photoUrl")?.takeIf { it.isNotBlank() } } catch (e: Exception) { null },
                    authProviders = try {
                        snapshot.get<Map<String, Boolean>?>("authProviders") ?: emptyMap()
                    } catch (e: Exception) { emptyMap() },
                    linkedServices = try {
                        snapshot.get<Map<String, Boolean>?>("linkedServices") ?: emptyMap()
                    } catch (e: Exception) { emptyMap() },
                    encryptedServiceAuth = try {
                        snapshot.get<Map<String, String>?>("encryptedServiceAuth") ?: snapshot.get<Map<String, String>?>("encryptedServiceTokens") ?: emptyMap()
                    } catch (e: Exception) { emptyMap() }
                )
            } else {
                null
            }
        }
    }

    override suspend fun getUserProfileRest(uid: String, idToken: String): UserProfileDto? {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        val data = try {
            handleAuthApi(name = "getUserProfileRest") {
                firestoreApiService.getFirestoreProfile(projectId, uid, idToken)
            }
        } catch (e: AuthApiException) {
            if (e.originalApiException.code == 404) return null
            throw e
        } catch (e: RemoteException.Api) {
            if (e.code == 404) return null
            throw e
        } ?: return null
        
        return UserProfileDto(
            username = (data["username"] as? String ?: data["displayName"] as? String)?.takeIf { it.isNotBlank() },
            email = (data["email"] as? String)?.takeIf { it.isNotBlank() },
            photoUrl = (data["photoUrl"] as? String)?.takeIf { it.isNotBlank() },
            authProviders = (data["authProviders"] as? Map<*, *>)?.map { it.key.toString() to (it.value as? Boolean ?: false) }?.toMap() ?: emptyMap(),
            linkedServices = (data["linkedServices"] as? Map<*, *>)?.map { it.key.toString() to (it.value as? Boolean ?: false) }?.toMap() ?: emptyMap(),
            encryptedServiceAuth = (data["encryptedServiceAuth"] as? Map<*, *>)?.map { it.key.toString() to it.value.toString() }?.toMap() 
                ?: (data["encryptedServiceTokens"] as? Map<*, *>)?.map { it.key.toString() to it.value.toString() }?.toMap() 
                ?: emptyMap()
        )
    }

    override suspend fun saveUserProfile(uid: String, profile: UserProfileDto) {
        val updateMap = mutableMapOf<String, Any?>()
        updateMap["username"] = profile.username
        updateMap["email"] = profile.email
        updateMap["photoUrl"] = profile.photoUrl
        updateMap["authProviders"] = profile.authProviders ?: emptyMap<String, Boolean>()
        updateMap["linkedServices"] = profile.linkedServices ?: emptyMap<String, Boolean>()
        profile.encryptedServiceAuth?.let { updateMap["encryptedServiceAuth"] = it }
        firestore.collection("users").document(uid).set(updateMap, merge = true)
    }

    override suspend fun saveUserProfileRest(uid: String, idToken: String, profile: UserProfileDto) {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        val fields = mutableMapOf<String, Any?>()
        val updateMask = mutableListOf<String>()

        fields["username"] = profile.username
        updateMask.add("username")
        fields["email"] = profile.email
        updateMask.add("email")
        fields["photoUrl"] = profile.photoUrl
        updateMask.add("photoUrl")
        fields["authProviders"] = profile.authProviders ?: emptyMap<String, Boolean>()
        updateMask.add("authProviders")
        fields["linkedServices"] = profile.linkedServices ?: emptyMap<String, Boolean>()
        updateMask.add("linkedServices")
        profile.encryptedServiceAuth?.let { 
            fields["encryptedServiceAuth"] = it
            updateMask.add("encryptedServiceAuth")
        }

        if (fields.isNotEmpty()) {
            handleAuthApi(name = "saveUserProfileRest") {
                firestoreApiService.saveFirestoreProfile(
                    projectId = projectId,
                    uid = uid,
                    idToken = idToken,
                    fields = fields,
                    fieldPaths = updateMask
                )
            }
        }
    }

    override suspend fun updateCustomField(uid: String, field: String, value: Boolean) {
        firestore.collection("users").document(uid).update("linkedServices.$field" to value)
    }

    override suspend fun updateCustomFieldRest(uid: String, idToken: String, field: String, value: Boolean) {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        val fields = mapOf("linkedServices" to mapOf(field to value))
        handleAuthApi(name = "updateCustomFieldRest") {
            firestoreApiService.saveFirestoreProfile(projectId, uid, idToken, fields, listOf("linkedServices.$field"))
        }
    }

    override suspend fun deleteUserProfile(uid: String) {
        firestore.collection("users").document(uid).delete()
    }

    override suspend fun deleteUserProfileRest(uid: String, idToken: String) {
        val projectId = firebaseProjectId ?: throw Exception("Firebase Project ID not found")
        try {
            handleAuthApi(name = "deleteUserProfileRest") {
                firestoreApiService.deleteFirestoreProfile(projectId, uid, idToken)
            }
        } catch (e: AuthApiException) {
            if (e.originalApiException.code != 404) throw e
        } catch (e: RemoteException.Api) {
            if (e.code != 404) throw e
        }
    }
}
