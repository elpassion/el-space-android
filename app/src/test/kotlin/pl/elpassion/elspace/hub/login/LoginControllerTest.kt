package pl.elpassion.elspace.hub.login

import com.elpassion.android.commons.rxjavatest.thenError
import com.elpassion.android.commons.rxjavatest.thenJust
import com.elpassion.android.commons.rxjavatest.thenNever
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Test
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers.trampoline
import rx.schedulers.TestScheduler

class LoginControllerTest {

    val api = mock<Login.HubTokenApi>()
    val view = mock<Login.View>()
    val loginRepository = mock<Login.Repository>()
    val shortcutService = mock<ShortcutService>()
    val subscribeOnScheduler = TestScheduler()

    @Test
    fun shouldOpenReportListScreenIfUserIsLoggedInOnCreate() {
        whenever(loginRepository.readToken()).thenReturn("token")
        createController().onCreate()
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenIfUserIsNotLoggedInOnCreate() {
        createController().onCreate()
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldSaveGivenTokenOnLogin() {
        val token = "token"
        createController().onLogin(token)
        verify(loginRepository).saveToken(token)
    }

    @Test
    fun shouldNotSaveGivenTokenOnLoginWhenTokenIsEmpty() {
        createController().onLogin("")
        verify(loginRepository, never()).saveToken(any())
    }

    @Test
    fun shouldShowErrorAboutEmptyTokenWhenTokenIsEmpty() {
        createController().onLogin("")
        verify(view).showEmptyLoginError()
    }

    @Test
    fun shouldNotShowErrorAboutEmptyTokenWhenTokenIsNotEmpty() {
        createController().onLogin("login")
        verify(view, never()).showEmptyLoginError()
    }

    @Test
    fun shouldOpenReportListScreenIfTokenIsNotEmptyOnLogin() {
        createController().onLogin("login")
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenIfTokenIsEmptyOnLogin() {
        createController().onLogin("")
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldCreateAppShortcutsWhenSupported() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(true)
        createController().onLogin("login")

        verify(shortcutService).creteAppShortcuts()
    }

    @Test
    fun shouldCreateAppShortcutsWhenLoggedWithGoogle() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(true)
        stubHubApiToReturnToken()
        createController().onGoogleToken()

        verify(shortcutService).creteAppShortcuts()
    }

    @Test
    fun shouldNotCreateAppShortcutsWhenDeviceNotSupported() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(false)
        createController().onLogin("login")

        verify(shortcutService, never()).creteAppShortcuts()
    }

    @Test
    fun shouldOpenHubWebsiteOnHub() {
        createController().onHub()
        verify(view).openHubWebsite()
    }

    @Test
    fun shouldAuthorizeInHubApiWithGoogleToken() {
        stubHubApiToReturnToken()
        createController().onGoogleToken()
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenWhenFetchingTokenFromHubApiFailed() {
        stubHubApiToReturnError()
        createController().onGoogleToken()
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldShowErrorWhenFetchingTokenFromHubApiFailed() {
        stubHubApiToReturnError()
        createController().onGoogleToken()
        verify(view).showError()
    }

    @Test
    fun shouldNotShowErrorWhenFetchingTokenFromHubApiSucceeded() {
        stubHubApiToReturnToken()
        createController().onGoogleToken()
        verify(view, never()).showError()
    }

    @Test
    fun shouldShowLoaderWhenFetchingTokenFromHubApi() {
        stubHubApiToNeverReturn()
        createController().onGoogleToken()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderAfterFetchingToken() {
        stubHubApiToReturnToken()
        createController().onGoogleToken()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderUntilFetchingTokenFinished() {
        stubHubApiToNeverReturn()
        createController().onGoogleToken()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldSaveTokenWhenFetchingTokenFromHubApiSucceeded() {
        stubHubApiToReturnToken()
        createController().onGoogleToken()
        verify(loginRepository).saveToken("token")
    }

    @Test
    fun shouldUnsubscribeOnDestroy() {
        var unsubscribed = false
        val observable = Observable.never<String>().doOnUnsubscribe { unsubscribed = true }
        whenever(api.loginWithGoogleToken()).thenReturn(observable)
        createController().run {
            onGoogleToken()
            onDestroy()
        }
        Assert.assertTrue(unsubscribed)
    }

    @Test
    fun shouldSubscribeOnGivenScheduler() {
        stubHubApiToReturnToken()
        createController(subscribeOnScheduler = subscribeOnScheduler).onGoogleToken()
        verify(view, never()).openReportListScreen()
        subscribeOnScheduler.triggerActions()
        verify(view).openReportListScreen()
    }

    fun createController(subscribeOnScheduler: Scheduler = trampoline()) =
            LoginController(view, loginRepository, shortcutService, api, subscribeOnScheduler)

    private fun stubHubApiToReturnToken() {
        whenever(api.loginWithGoogleToken()).thenJust("token")
    }

    private fun stubHubApiToNeverReturn() {
        whenever(api.loginWithGoogleToken()).thenNever()
    }

    private fun stubHubApiToReturnError() {
        whenever(api.loginWithGoogleToken()).thenError(RuntimeException())
    }
}
