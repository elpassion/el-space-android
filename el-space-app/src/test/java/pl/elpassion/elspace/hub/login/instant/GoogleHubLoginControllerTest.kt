package pl.elpassion.elspace.hub.login.instant

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.Single.error
import io.reactivex.Single.just
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.mockito.stubbing.OngoingStubbing
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.login.HubTokenFromApi
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class GoogleHubLoginControllerTest {

    val view = mock<GoogleHubLogin.View>()
    val repository = mock<GoogleHubLogin.Repository>()
    val api = mock<GoogleHubLogin.Api>()
    val shortcutService = mock<ShortcutService>()
    val controller = GoogleHubLoginController(view, repository, api, shortcutService, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))

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
        stubApi().thenJustToken("token")
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(view).openOnLoggedInScreen()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenWhenGoogleLoginFailed() {
        controller.onGoogleSignIn(isSuccess = false, googleToken = null)
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldShowGoogleLoginErrorWhenGoogleLoginFailed() {
        controller.onGoogleSignIn(isSuccess = false, googleToken = null)
        verify(view).showGoogleLoginError()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenWhenGoogleLoginSucceedButApiCallFails() {
        stubApi().thenError()
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(view, never()).openOnLoggedInScreen()
    }

    @Test
    fun shouldShowApiLoginErrorWhenGoogleLoginSucceedButApiCallFails() {
        stubApi().thenError()
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(view).showApiLoginError()
    }

    @Test
    fun shouldPersistTokenInRepository() {
        stubApi().thenJustToken("token")
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(repository).saveToken("token")
    }

    @Test
    fun shouldMakeCallOnBackgroundScheduler() {
        val backgroundScheduler = TestScheduler()
        val schedulers = SchedulersSupplier(backgroundScheduler, Schedulers.trampoline())
        stubApi().thenJustToken("token")
        GoogleHubLoginController(view, repository, api, shortcutService, schedulers).onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(repository, never()).saveToken("token")
        backgroundScheduler.triggerActions()
        verify(repository).saveToken("token")
    }

    @Test
    fun shouldObserveCallOnUiScheduler() {
        val uiScheduler = TestScheduler()
        val schedulers = SchedulersSupplier(Schedulers.trampoline(), uiScheduler)
        stubApi().thenJustToken("token")
        GoogleHubLoginController(view, repository, api, shortcutService, schedulers).onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(repository, never()).saveToken("token")
        uiScheduler.triggerActions()
        verify(repository).saveToken("token")
    }

    @Test
    fun shouldCreateShortcutAfterFirstSuccessfulLogin() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(true)
        stubApi().thenJustToken("token")
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(shortcutService).creteAppShortcuts()
    }

    @Test
    fun shouldNotCreateShortcutAfterFirstSuccessfulLoginIfNotSupported() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(false)
        stubApi().thenJustToken("token")
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(shortcutService, never()).creteAppShortcuts()
    }

    @Test
    fun shouldLogoutFromGoogleWhenApiCallFails() {
        stubApi().thenError()
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(view).logoutFromGoogle()
    }

    private fun GoogleHubLoginController.onGoogleSignIn(isSuccess: Boolean, googleToken: String?) = onGoogleSignInResult(GoogleHubLogin.HubGoogleSignInResult(isSuccess, googleToken))

    private fun stubApi() = whenever(api.loginWithGoogle(any()))

    private fun stubRepository() = whenever(repository.readToken())
}

private fun <T> OngoingStubbing<Single<T>>.thenError() {
    thenReturn(error(RuntimeException()))
}

private fun OngoingStubbing<Single<HubTokenFromApi>>.thenJustToken(token: String) {
    thenReturn(just(HubTokenFromApi(token)))
}
