package pl.elpassion.elspace.hub.login.instant

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.Single.error
import io.reactivex.Single.just
import org.junit.Test
import org.mockito.stubbing.OngoingStubbing

class InstantGoogleHubLoginControllerTest {

    val view = mock<InstantGoogleHubLogin.View>()
    val repository = mock<InstantGoogleHubLogin.Repository>()
    val api = mock<InstantGoogleHubLogin.Api>()
    val controller = InstantGoogleHubLoginController(view, repository, api)

    @Test
    fun shouldOpenOnLoggedInScreenIfUserIsLoggedInOnCreate() {
        stubRepositoryToReturn("token")
        controller.onCreate()
        verify(view).openOnLoggedInScreen()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenIfUserIsNotLoggedInOnCreate() {
        stubRepositoryToReturn(null)
        controller.onCreate()
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldStartGoogleLoginIntentOnCreate() {
        stubRepositoryToReturn(null)
        controller.onCreate()
        verify(view).startGoogleLoginIntent()
    }

    @Test
    fun shouldOpenOnLoggedInScreenWhenGoogleLoginSucceed() {
        whenever(api.loginWithGoogle(any())).thenJust("token")
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(view).openOnLoggedInScreen()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenWhenGoogleLoginFailed() {
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = false, googleToken = null))
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldShowGoogleLoginErrorWhenGoogleLoginFailed() {
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = false, googleToken = null))
        verify(view).showGoogleLoginError()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenWhenGoogleLoginSucceedButApiCallFails() {
        whenever(api.loginWithGoogle(any())).thenError()
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldShowApiLoginErrorWhenGoogleLoginSucceedButApiCallFails() {
        whenever(api.loginWithGoogle(any())).thenError()
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(view).showApiLoginError()
    }

    @Test
    fun shouldPersistTokenInRepository() {
        whenever(api.loginWithGoogle(any())).thenJust("token")
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(repository).saveToken("token")
    }

    private fun stubRepositoryToReturn(token: String?) {
        whenever(repository.readToken()).thenReturn(token)
    }
}

private fun <T> OngoingStubbing<Single<T>>.thenError() {
    thenReturn(error(RuntimeException()))
}

private fun <T> OngoingStubbing<Single<T>>.thenJust(value: T) {
    thenReturn(just(value))
}
