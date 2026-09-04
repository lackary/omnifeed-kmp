package io.lackstudio.omnifeed.auth.data.local.source

import io.lackstudio.omnifeed.auth.data.local.model.UserServiceTokens
import io.lackstudio.omnifeed.auth.data.storage.*
import io.lackstudio.omnifeed.auth.domain.model.User
import io.mockk.*
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test
import kotlin.test.*

class AuthLocalDataSourceImplTest {

    private val userCacheStorage = mockk<LocalStorage>(relaxed = true)
    private val serviceTokenStorage = mockk<LocalStorage>(relaxed = true)
    private lateinit var dataSource: AuthLocalDataSourceImpl

    @BeforeTest
    fun setup() {
        // Mock the static extension functions by mocking the LocalStorage interface calls
        // Since getFireBaseAuth calls getDirectOrNull(FIREBASE_AUTH_USER_KEY)
        every { userCacheStorage.getStringDirectOrNull(FIREBASE_AUTH_USER_KEY) } returns null
        
        dataSource = AuthLocalDataSourceImpl(userCacheStorage, serviceTokenStorage)
    }

    @Test
    fun `saveUser should update storage and flow`() = runTest {
        val user = User(id = "uid123", email = "test@test.com", username = null, photoUrl = null)
        
        dataSource.userFlow.test {
            assertEquals(null, awaitItem()) // Initial value
            
            dataSource.saveUser(user)
            
            assertEquals(user, awaitItem())
            verify { userCacheStorage.saveStringDirect(FIREBASE_AUTH_USER_KEY, any()) }
        }
    }

    @Test
    fun `saveServiceToken should append to existing tokens`() = runTest {
        val userId = "uid123"
        val existingTokens = UserServiceTokens(userId, mapOf("service1" to "token1"))
        
        // Mock getting existing tokens
        coEvery { serviceTokenStorage.getStringOrNull(userId) } returns localStorageJson.encodeToString(existingTokens)
        
        dataSource.saveServiceToken(userId, "service2", "token2")
        
        // Verify save was called with both tokens
        coVerify { 
            serviceTokenStorage.saveString(userId, withArg { 
                assertTrue(it.contains("service1"))
                assertTrue(it.contains("service2"))
                assertTrue(it.contains("token1"))
                assertTrue(it.contains("token2"))
            }) 
        }
    }

    @Test
    fun `getServiceToken should return null if user not found`() = runTest {
        coEvery { serviceTokenStorage.getStringOrNull("unknown") } returns null
        
        val token = dataSource.getServiceToken("unknown", "service")
        
        assertNull(token)
    }

    @Test
    fun `clearServiceToken should remove only specified service`() = runTest {
        val userId = "uid123"
        val existingTokens = UserServiceTokens(userId, mapOf("s1" to "t1", "s2" to "t2"))
        coEvery { serviceTokenStorage.getStringOrNull(userId) } returns localStorageJson.encodeToString(existingTokens)
        
        dataSource.clearServiceToken(userId, "s1")
        
        coVerify {
            serviceTokenStorage.saveString(userId, withArg {
                assertFalse(it.contains("s1"))
                assertTrue(it.contains("s2"))
            })
        }
    }

    @Test
    fun `getUser should return current cached user`() = runTest {
        val user = User(id = "uid123", email = "test@test.com", username = null, photoUrl = null)
        dataSource.saveUser(user)
        
        val result = dataSource.getUser()
        
        assertEquals(user, result)
    }

    @Test
    fun `clearAllServiceTokens should delegate to storage`() = runTest {
        dataSource.clearAllServiceTokens()
        
        coVerify { serviceTokenStorage.clearAll() }
    }

    @Test
    fun `initialization should load user from storage`() = runTest {
        val user = User(id = "cached", email = "cached@test.com", username = null, photoUrl = null)
        val storage = mockk<LocalStorage>(relaxed = true)
        every { storage.getStringDirectOrNull(FIREBASE_AUTH_USER_KEY) } returns localStorageJson.encodeToString(user)
        
        val newDataSource = AuthLocalDataSourceImpl(storage, serviceTokenStorage)
        
        assertEquals(user, newDataSource.getUser())
    }
}
