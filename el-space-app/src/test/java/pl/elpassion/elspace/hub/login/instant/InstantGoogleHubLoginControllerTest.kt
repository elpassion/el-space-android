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
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class InstantGoogleHubLoginControllerTest {

    val view = mock<InstantGoogleHubLogin.View>()
    val repository = mock<InstantGoogleHubLogin.Repository>()
    val api = mock<InstantGoogleHubLogin.Api>()
    val shortcutService = mock<ShortcutService>()
    val controller = InstantGoogleHubLoginController(view, repository, api, shortcutService, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))

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
        stubApi().thenJust("token")
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(repository).saveToken("token")
    }

    @Test
    fun shouldMakeCallOnBackgroundScheduler() {
        val backgroundScheduler = TestScheduler()
        val schedulers = SchedulersSupplier(backgroundScheduler, Schedulers.trampoline())
        stubApi().thenJust("token")
        InstantGoogleHubLoginController(view, repository, api, shortcutService, schedulers).onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(repository, never()).saveToken("token")
        backgroundScheduler.triggerActions()
        verify(repository).saveToken("token")
    }

    @Test
    fun shouldObserveCallOnUiScheduler() {
        val uiScheduler = TestScheduler()
        val schedulers = SchedulersSupplier(Schedulers.trampoline(), uiScheduler)
        stubApi().thenJust("token")
        InstantGoogleHubLoginController(view, repository, api, shortcutService, schedulers).onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(repository, never()).saveToken("token")
        uiScheduler.triggerActions()
        verify(repository).saveToken("token")
    }

    @Test
    fun createShortcutAfterFirstSuccessfulLogin() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(true)
        stubApi().thenJust("token")
        controller.onGoogleSignIn(isSuccess = true, googleToken = "googleToken")
        verify(shortcutService).creteAppShortcuts()
    }

    private fun InstantGoogleHubLoginController.onGoogleSignIn(isSuccess: Boolean, googleToken: String?) = onGoogleSignInResult(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess, googleToken))

    private fun stubApi() = whenever(api.loginWithGoogle(any()))

    private fun stubRepository() = whenever(repository.readToken())
}

private fun <T> OngoingStubbing<Single<T>>.thenError() {
    thenReturn(error(RuntimeException()))
}

private fun <T> OngoingStubbing<Single<T>>.thenJust(value: T) {
    thenReturn(just(value))
}
