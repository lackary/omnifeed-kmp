package io.lackstudio.omnifeed.auth.domain.usecase

import io.lackstudio.omnifeed.auth.domain.model.User
import io.lackstudio.omnifeed.auth.domain.repository.AuthRepository
import io.lackstudio.omnifeed.core.domain.usecase.UseCaseResult
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthUseCasesTest {

    private val repository = mockk<AuthRepository>()
    private val testUser = User(
        id = "test-id",
        email = "test@example.com",
        username = "testuser",
        photoUrl = null
    )

    @BeforeTest
    fun setUp() {
        // Clear mocks before each test
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(repository)
        clearMocks(repository)
    }

    // --- Authentication Use Cases ---

    @Test
    fun `SignInWithEmailUseCase calls repository and returns success`() = runTest {
        val useCase = SignInWithEmailUseCase(repository)
        coEvery { repository.signInWithEmail("email", "password") } returns testUser

        val result = useCase("email", "password")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.signInWithEmail("email", "password") }
    }

    @Test
    fun `SignInWithEmailUseCase returns error when repository fails`() = runTest {
        val useCase = SignInWithEmailUseCase(repository)
        val exception = RuntimeException("Login failed")
        coEvery { repository.signInWithEmail(any(), any()) } throws exception

        val result = useCase("email", "password")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.signInWithEmail("email", "password") }
    }

    @Test
    fun `SignUpWithEmailUseCase calls repository and returns success`() = runTest {
        val useCase = SignUpWithEmailUseCase(repository)
        coEvery { repository.signUpWithEmail("email", "password", "username") } returns testUser

        val result = useCase("email", "password", "username")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.signUpWithEmail("email", "password", "username") }
    }

    @Test
    fun `SignUpWithEmailUseCase returns error when repository fails`() = runTest {
        val useCase = SignUpWithEmailUseCase(repository)
        val exception = RuntimeException("Registration failed")
        coEvery { repository.signUpWithEmail(any(), any(), any()) } throws exception

        val result = useCase("email", "password", "username")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.signUpWithEmail("email", "password", "username") }
    }

    @Test
    fun `SignInWithGoogleUseCase calls repository and returns success`() = runTest {
        val useCase = SignInWithGoogleUseCase(repository)
        coEvery { repository.signInWithGoogle("token", "access") } returns testUser

        val result = useCase("token", "access")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.signInWithGoogle("token", "access") }
    }

    @Test
    fun `SignInWithGoogleUseCase returns error when repository fails`() = runTest {
        val useCase = SignInWithGoogleUseCase(repository)
        val exception = RuntimeException("Google sign in failed")
        coEvery { repository.signInWithGoogle(any(), any()) } throws exception

        val result = useCase("token", "access")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.signInWithGoogle("token", "access") }
    }

    @Test
    fun `SignInWithCustomServiceUseCase calls repository and returns success`() = runTest {
        val useCase = SignInWithCustomServiceUseCase(repository)
        coEvery { repository.signInWithCustomService("service", "token") } returns testUser

        val result = useCase("service", "token")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.signInWithCustomService("service", "token") }
    }

    @Test
    fun `SignInWithCustomServiceUseCase returns error when repository fails`() = runTest {
        val useCase = SignInWithCustomServiceUseCase(repository)
        val exception = RuntimeException("Custom service sign in failed")
        coEvery { repository.signInWithCustomService(any(), any()) } throws exception

        val result = useCase("service", "token")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.signInWithCustomService("service", "token") }
    }

    @Test
    fun `SignOutUseCase calls repository and returns success`() = runTest {
        val useCase = SignOutUseCase(repository)
        coEvery { repository.signOut() } just Runs

        val result = useCase()

        assertTrue(result.isSuccess)
        coVerify { repository.signOut() }
    }

    @Test
    fun `SignOutUseCase returns failure when repository fails`() = runTest {
        val useCase = SignOutUseCase(repository)
        val exception = RuntimeException("Sign out failed")
        coEvery { repository.signOut() } throws exception

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { repository.signOut() }
    }

    @Test
    fun `DeleteAccountUseCase calls repository and returns success`() = runTest {
        val useCase = DeleteAccountUseCase(repository)
        coEvery { repository.deleteAccount() } just Runs

        val result = useCase()

        assertTrue(result is UseCaseResult.Success)
        coVerify { repository.deleteAccount() }
    }

    @Test
    fun `DeleteAccountUseCase returns error when repository fails`() = runTest {
        val useCase = DeleteAccountUseCase(repository)
        val exception = RuntimeException("Delete account failed")
        coEvery { repository.deleteAccount() } throws exception

        val result = useCase()

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.deleteAccount() }
    }

    // --- User Observation and Update Use Cases ---

    @Test
    fun `ObserveUserUseCase returns flow from repository`() = runTest {
        val useCase = ObserveUserUseCase(repository)
        val userFlow = flowOf(testUser)
        every { repository.currentUser } returns userFlow

        val result = useCase()

        assertEquals(userFlow, result)
        verify { repository.currentUser }
    }

    @Test
    fun `ObserveUserUseCase returns null flow from repository`() = runTest {
        val useCase = ObserveUserUseCase(repository)
        val userFlow = flowOf(null)
        every { repository.currentUser } returns userFlow

        val result = useCase()

        assertEquals(userFlow, result)
        verify { repository.currentUser }
    }

    @Test
    fun `UpdateUsernameUseCase calls repository and returns success`() = runTest {
        val useCase = UpdateUsernameUseCase(repository)
        coEvery { repository.updateUsername("newname") } returns testUser

        val result = useCase("newname")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.updateUsername("newname") }
    }

    @Test
    fun `UpdateUsernameUseCase returns error when repository fails`() = runTest {
        val useCase = UpdateUsernameUseCase(repository)
        val exception = RuntimeException("Update username failed")
        coEvery { repository.updateUsername(any()) } throws exception

        val result = useCase("newname")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.updateUsername("newname") }
    }

    @Test
    fun `UpdatePasswordUseCase calls repository and returns success`() = runTest {
        val useCase = UpdatePasswordUseCase(repository)
        coEvery { repository.updatePassword("new", "old") } just Runs

        val result = useCase("new", "old")

        assertTrue(result is UseCaseResult.Success)
        coVerify { repository.updatePassword("new", "old") }
    }

    @Test
    fun `UpdatePasswordUseCase returns error when repository fails`() = runTest {
        val useCase = UpdatePasswordUseCase(repository)
        val exception = RuntimeException("Update password failed")
        coEvery { repository.updatePassword(any(), any()) } throws exception

        val result = useCase("new", "old")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.updatePassword("new", "old") }
    }

    // --- Provider Management Use Cases ---

    @Test
    fun `LinkWithEmailUseCase calls repository and returns success`() = runTest {
        val useCase = LinkWithEmailUseCase(repository)
        coEvery { repository.linkWithEmail("email", "password") } returns testUser

        val result = useCase("email", "password")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.linkWithEmail("email", "password") }
    }

    @Test
    fun `LinkWithEmailUseCase returns error when repository fails`() = runTest {
        val useCase = LinkWithEmailUseCase(repository)
        val exception = RuntimeException("Link email failed")
        coEvery { repository.linkWithEmail(any(), any()) } throws exception

        val result = useCase("email", "password")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.linkWithEmail("email", "password") }
    }

    @Test
    fun `LinkWithGoogleUseCase calls repository and returns success`() = runTest {
        val useCase = LinkWithGoogleUseCase(repository)
        coEvery { repository.linkWithGoogle("token", "access") } returns testUser

        val result = useCase("token", "access")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.linkWithGoogle("token", "access") }
    }

    @Test
    fun `LinkWithGoogleUseCase returns error when repository fails`() = runTest {
        val useCase = LinkWithGoogleUseCase(repository)
        val exception = RuntimeException("Link Google failed")
        coEvery { repository.linkWithGoogle(any(), any()) } throws exception

        val result = useCase("token", "access")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.linkWithGoogle("token", "access") }
    }

    @Test
    fun `LinkWithCustomServiceUseCase calls repository and returns success`() = runTest {
        val useCase = LinkWithCustomServiceUseCase(repository)
        coEvery { repository.linkWithCustomService("service", "token") } returns testUser

        val result = useCase("service", "token")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.linkWithCustomService("service", "token") }
    }

    @Test
    fun `LinkWithCustomServiceUseCase returns error when repository fails`() = runTest {
        val useCase = LinkWithCustomServiceUseCase(repository)
        val exception = RuntimeException("Link custom service failed")
        coEvery { repository.linkWithCustomService(any(), any()) } throws exception

        val result = useCase("service", "token")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.linkWithCustomService("service", "token") }
    }

    @Test
    fun `UnlinkProviderUseCase calls repository and returns success`() = runTest {
        val useCase = UnlinkProviderUseCase(repository)
        coEvery { repository.unlinkProvider("provider") } returns testUser

        val result = useCase("provider")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.unlinkProvider("provider") }
    }

    @Test
    fun `UnlinkProviderUseCase returns error when repository fails`() = runTest {
        val useCase = UnlinkProviderUseCase(repository)
        val exception = RuntimeException("Unlink provider failed")
        coEvery { repository.unlinkProvider(any()) } throws exception

        val result = useCase("provider")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.unlinkProvider("provider") }
    }

    @Test
    fun `UnlinkCustomServiceUseCase calls repository and returns success`() = runTest {
        val useCase = UnlinkCustomServiceUseCase(repository)
        coEvery { repository.unlinkCustomService("service") } returns testUser

        val result = useCase("service")

        assertTrue(result is UseCaseResult.Success)
        assertEquals(testUser, result.data)
        coVerify { repository.unlinkCustomService("service") }
    }

    @Test
    fun `UnlinkCustomServiceUseCase returns error when repository fails`() = runTest {
        val useCase = UnlinkCustomServiceUseCase(repository)
        val exception = RuntimeException("Unlink custom service failed")
        coEvery { repository.unlinkCustomService(any()) } throws exception

        val result = useCase("service")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.unlinkCustomService("service") }
    }

    // --- Re-authentication Use Cases ---

    @Test
    fun `ReauthenticateWithEmailUseCase calls repository and returns success`() = runTest {
        val useCase = ReauthenticateWithEmailUseCase(repository)
        coEvery { repository.reauthenticateWithEmail("password") } just Runs

        val result = useCase("password")

        assertTrue(result is UseCaseResult.Success)
        coVerify { repository.reauthenticateWithEmail("password") }
    }

    @Test
    fun `ReauthenticateWithEmailUseCase returns error when repository fails`() = runTest {
        val useCase = ReauthenticateWithEmailUseCase(repository)
        val exception = RuntimeException("Reauth email failed")
        coEvery { repository.reauthenticateWithEmail(any()) } throws exception

        val result = useCase("password")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.reauthenticateWithEmail("password") }
    }

    @Test
    fun `ReauthenticateWithGoogleUseCase calls repository and returns success`() = runTest {
        val useCase = ReauthenticateWithGoogleUseCase(repository)
        coEvery { repository.reauthenticateWithGoogle("token", "access") } just Runs

        val result = useCase("token", "access")

        assertTrue(result is UseCaseResult.Success)
        coVerify { repository.reauthenticateWithGoogle("token", "access") }
    }

    @Test
    fun `ReauthenticateWithGoogleUseCase returns error when repository fails`() = runTest {
        val useCase = ReauthenticateWithGoogleUseCase(repository)
        val exception = RuntimeException("Reauth Google failed")
        coEvery { repository.reauthenticateWithGoogle(any(), any()) } throws exception

        val result = useCase("token", "access")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.reauthenticateWithGoogle("token", "access") }
    }

    @Test
    fun `ReauthenticateWithCustomServiceUseCase calls repository and returns success`() = runTest {
        val useCase = ReauthenticateWithCustomServiceUseCase(repository)
        coEvery { repository.reauthenticateWithCustomService("service", "token") } just Runs

        val result = useCase("service", "token")

        assertTrue(result is UseCaseResult.Success)
        coVerify { repository.reauthenticateWithCustomService("service", "token") }
    }

    @Test
    fun `ReauthenticateWithCustomServiceUseCase returns error when repository fails`() = runTest {
        val useCase = ReauthenticateWithCustomServiceUseCase(repository)
        val exception = RuntimeException("Reauth custom service failed")
        coEvery { repository.reauthenticateWithCustomService(any(), any()) } throws exception

        val result = useCase("service", "token")

        assertTrue(result is UseCaseResult.Error)
        assertEquals(exception, result.exception)
        coVerify { repository.reauthenticateWithCustomService("service", "token") }
    }
}
