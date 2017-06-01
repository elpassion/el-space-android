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
        stubRepository().thenReturn("token")
        controller.onCreate()
        verify(view).openOnLoggedInScreen()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenIfUserIsNotLoggedInOnCreate() {
        stubRepository().thenReturn(null)
        controller.onCreate()
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldStartGoogleLoginIntentOnCreate() {
        stubRepository().thenReturn(null)
        controller.onCreate()
        verify(view).startGoogleLoginIntent()
    }

    @Test
    fun shouldOpenOnLoggedInScreenWhenGoogleLoginSucceed() {
        stubApi().thenJust("token")
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
        stubApi().thenError()
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldShowApiLoginErrorWhenGoogleLoginSucceedButApiCallFails() {
        stubApi().thenError()
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(view).showApiLoginError()
    }

    @Test
    fun shouldPersistTokenInRepository() {
        stubApi().thenJust("token")
        controller.onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
        verify(repository).saveToken("token")
    }

    private fun stubApi() = whenever(api.loginWithGoogle(any()))

    private fun stubRepository() = whenever(repository.readToken())
}

private fun <T> OngoingStubbing<Single<T>>.thenError() {
    thenReturn(error(RuntimeException()))
}

private fun <T> OngoingStubbing<Single<T>>.thenJust(value: T) {
    thenReturn(just(value))
}
