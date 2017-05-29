package pl.elpassion.elspace.hub.login

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers.trampoline
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class HubLoginControllerTest {

    val api = mock<HubLogin.TokenApi>()
    val view = mock<HubLogin.View>()
    val loginRepository = mock<HubLogin.Repository>()
    val shortcutService = mock<ShortcutService>()
    val subscribeOnScheduler = TestScheduler()
    val observeOnScheduler = TestScheduler()
    private val correctSignInResult= createGoogleSingInResult(isSuccess = true, idToken = "google token")
    private val apiSubject = PublishSubject.create<HubTokenFromApi>()

    @Before
    fun setUp() {
        whenever(api.loginWithGoogleToken(any())).thenReturn(apiSubject)
    }

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
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onNext(HubTokenFromApi("token"))
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
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onNext(HubTokenFromApi("token"))
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenWhenFetchingTokenFromHubApiFailed() {
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onError(RuntimeException())
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldShowErrorWhenFetchingTokenFromHubApiFailed() {
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onError(RuntimeException())
        verify(view).showGoogleTokenError()
    }

    @Test
    fun shouldNotShowErrorWhenFetchingTokenFromHubApiSucceeded() {
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onNext(HubTokenFromApi("token"))
        verify(view, never()).showGoogleTokenError()
    }

    @Test
    fun shouldShowLoaderWhenFetchingTokenFromHubApi() {
        createController().onGoogleSignInResult(correctSignInResult)
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderAfterFetchingToken() {
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onNext(HubTokenFromApi("token"))
        apiSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderUntilFetchingTokenFinished() {
        createController().onGoogleSignInResult(correctSignInResult)
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldSaveTokenWhenFetchingTokenFromHubApiSucceeded() {
        createController().onGoogleSignInResult(correctSignInResult)
        apiSubject.onNext(HubTokenFromApi("token"))
        verify(loginRepository).saveToken("token")
    }

    @Test
    fun shouldUnsubscribeOnDestroy() {
        var unsubscribed = false
        val observable = Observable.never<HubTokenFromApi>().doFinally { unsubscribed = true }
        whenever(api.loginWithGoogleToken(GoogleTokenForHubTokenApi("google token"))).thenReturn(observable)
        createController().run {
            onGoogleSignInResult(correctSignInResult)
            onDestroy()
        }
        Assert.assertTrue(unsubscribed)
    }

    @Test
    fun shouldSubscribeOnGivenScheduler() {
        createController(subscribeOn = subscribeOnScheduler).onGoogleSignInResult(correctSignInResult)
        verify(view, never()).openReportListScreen()
        subscribeOnScheduler.triggerActions()
        apiSubject.onNext(HubTokenFromApi("token"))
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldObserveOnGivenScheduler() {
        createController(observeOn = observeOnScheduler).onGoogleSignInResult(correctSignInResult)
        apiSubject.onNext(HubTokenFromApi("token"))
        verify(view, never()).openReportListScreen()
        observeOnScheduler.triggerActions()
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldShowGoogleTokenErrorWhenSignInEndsWithFailure() {
        createController().onGoogleSignInResult(createGoogleSingInResult(isSuccess = false))
        verify(view).showGoogleTokenError()
    }

    @Test
    fun shouldNotShowGoogleTokenErrorWhenSignInEndsWithSuccessAndIdTokenIsNotNull() {
        createController().onGoogleSignInResult(createGoogleSingInResult(isSuccess = true, idToken = "idToken"))
        verify(view, never()).showGoogleTokenError()
    }

    @Test
    fun shouldShowGoogleTokenErrorWhenSignInEndsWithSuccessButIdTokenIsNull() {
        createController().onGoogleSignInResult(createGoogleSingInResult(isSuccess = true, idToken = null))
        verify(view).showGoogleTokenError()
    }

    private fun createGoogleSingInResult(isSuccess: Boolean = true, idToken: String? = null): ELPGoogleSignInResult {
        return object : ELPGoogleSignInResult {
            override val isSuccess: Boolean = isSuccess
            override val idToken: String? = idToken
        }
    }

    fun createController(subscribeOn: Scheduler = trampoline(), observeOn: Scheduler = trampoline()) =
            HubLoginController(view, loginRepository, shortcutService, api, SchedulersSupplier(subscribeOn, observeOn))

}
