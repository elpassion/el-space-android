package pl.elpassion.elspace.debate.login

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebateTokenRepository
import pl.elpassion.elspace.debate.login.DebateLogin.Api.LoginResponse

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val tokenRepo = mock<DebateTokenRepository>()
    private val loginApi = mock<DebateLogin.Api>()
    private val controller = DebateLoginController(view, tokenRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))
    private val apiSubject = SingleSubject.create<DebateLogin.Api.LoginResponse>()

    @Before
    fun setUp() {
        whenever(tokenRepo.hasToken(any())).thenReturn(false)
        whenever(loginApi.login(any())).thenReturn(apiSubject)
    }

    @Test
    fun shouldSaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12348")
        returnTokenFromApi("authToken")
        verify(tokenRepo).saveDebateToken(debateCode = "12348", authToken = "authToken")
    }

    @Test
    fun shouldReallySaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("realAuthToken")
        verify(tokenRepo).saveDebateToken(debateCode = "12345", authToken = "realAuthToken")
    }

    @Test
    fun shouldShowErrorIfLoginFails() {
        controller.onLogToDebate("error")
        apiSubject.onError(RuntimeException())
        verify(view).showLoginFailedError()
    }

    @Test
    fun shouldNotShowErrorIfLoginSucceed() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("authToken")
        verify(view, never()).showLoginFailedError()
    }

    @Test
    fun shouldShowLoaderOnLoginStart() {
        logToDebate(debateCode = "12345")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnLoginEnd() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("authToken")
        verify(view).hideLoader()
    }


    @Test
    fun shouldNotHideLoaderWhenLoginIsStillInProgress() {
        logToDebate(debateCode = "12345")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfCallIsStillInProgress() {
        logToDebate(debateCode = "12345")
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfCallIsNotInProgress() {
        controller.onDestroy()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldOpenDebateScreenOnLoginSuccess() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("authToken")
        verify(view).openDebateScreen("authToken")
    }

    @Test
    fun shouldNotOpenDebateScreenOnLoginFailure() {
        logToDebate(debateCode = "123")
        apiSubject.onError(RuntimeException())
        verify(view, never()).openDebateScreen(any())
    }

    @Test
    fun shouldOpenDebateScreenWithAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        forCodeReturnTokenFromRepo(debateCode = "12345", token = "token")
        logToDebate("12345")
        verify(view).openDebateScreen("token")
    }

    @Test
    fun shouldOpenDebateScreenWithRealAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        forCodeReturnTokenFromRepo(debateCode = "23456", token = "authToken")
        logToDebate("23456")
        verify(view).openDebateScreen("authToken")
    }

    @Test
    fun shouldShowLoaderOnLoggingWithTokenFromRepository() {
        forCodeReturnTokenFromRepo(debateCode = "23456", token = "authToken")
        logToDebate("23456")
        verify(view).showLoader()
    }

    @Test
    fun shouldShowWrongPinErrorWhenPinHasLessDigitsThan5() {
        logToDebate("")
        verify(view).showWrongPinError()
    }

    @Test
    fun shouldShowWrongPinErrorWhenPinReallyHasLessDigitsThan5() {
        logToDebate("1234")
        verify(view).showWrongPinError()
    }

    @Test
    fun shouldNotShowWrongPinErrorWhenPinHas5Digits() {
        forCodeReturnTokenFromRepo(debateCode = "23456", token = "authToken")
        logToDebate("23456")
        verify(view, never()).showWrongPinError()
    }

    @Test
    fun shouldUseGivenSchedulerForSubscribeOnInApiCall() {
        val subscribeOn = TestScheduler()
        val controller = DebateLoginController(view, tokenRepo, loginApi, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.onLogToDebate("12345")
        returnTokenFromApi("authToken")
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerForObserveOnInApiCall() {
        val observeOn = TestScheduler()
        val controller = DebateLoginController(view, tokenRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.onLogToDebate("12345")
        returnTokenFromApi("authToken")
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldSaveDebateCode() {
        controller.onLogToDebate("12345")
        verify(tokenRepo).saveLatestDebateCode("12345")
    }

    @Test
    fun shouldNotFillLatestDebateCodeWhenNotSaved() {
        whenever(tokenRepo.getLatestDebateCode()).thenReturn(null)
        controller.onCreate()
        verify(view, never()).fillDebateCode()
    }

    private fun forCodeReturnTokenFromRepo(debateCode: String, token: String) {
        whenever(tokenRepo.hasToken(debateCode = debateCode)).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate(debateCode = debateCode)).thenReturn(token)
    }

    private fun returnTokenFromApi(token: String) {
        apiSubject.onSuccess(LoginResponse(token))
    }

    private fun logToDebate(debateCode: String = "12345") {
        controller.onLogToDebate(debateCode)
    }
}
