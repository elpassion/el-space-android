package pl.elpassion.elspace.debate.login

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.debate.AuthToken
import pl.elpassion.elspace.debate.DebatesRepository

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val debateRepo = mock<DebatesRepository>()
    private val loginApi = mock<DebateLogin.Api>()
    private val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))
    private val apiSubject = SingleSubject.create<AuthToken>()

    @Before
    fun setUp() {
        whenever(debateRepo.hasToken(any())).thenReturn(false)
        whenever(loginApi.login(any())).thenReturn(apiSubject)
    }

    @Test
    fun shouldSaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12348")
        returnTokenFromApi("authToken", "userId")
        verify(debateRepo).saveDebateToken(debateCode = "12348", authToken = AuthToken("authToken", "userId"))
    }

    @Test
    fun shouldReallySaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("realAuthToken", "realUserId")
        verify(debateRepo).saveDebateToken(debateCode = "12345", authToken = AuthToken("realAuthToken", "realUserId"))
    }

    @Test
    fun shouldShowDebateClosedErrorOnLogin406CodeErrorFromApi() {
        logToDebate()
        apiSubject.onError(createHttpException(406))
        verify(view).showDebateClosedError()
    }

    @Test
    fun shouldShowErrorOnLoginError() {
        controller.onLogToDebate("error")
        val error = RuntimeException()
        apiSubject.onError(error)
        verify(view).showLoginError(error)
    }

    @Test
    fun shouldNotShowErrorIfLoginSucceed() {
        logToDebate()
        returnTokenFromApi("authToken", "userId")
        verify(view, never()).showLoginError(any())
    }

    @Test
    fun shouldShowLoaderOnLoginStart() {
        logToDebate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnLoginEnd() {
        logToDebate()
        returnTokenFromApi("authToken", "userId")
        verify(view).hideLoader()
    }


    @Test
    fun shouldNotHideLoaderWhenLoginIsStillInProgress() {
        logToDebate()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfCallIsStillInProgress() {
        logToDebate()
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
        logToDebate()
        returnTokenFromApi("authToken", "userId")
        verify(view).openDebateScreen(AuthToken("authToken", "userId"))
    }

    @Test
    fun shouldNotOpenDebateScreenOnLoginFailure() {
        logToDebate(debateCode = "123")
        apiSubject.onError(RuntimeException())
        verify(view, never()).openDebateScreen(any())
    }

    @Test
    fun shouldOpenDebateScreenWithAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        val token = AuthToken("token", "userId")
        forCodeReturnTokenFromRepo(debateCode = "12345", token = token)
        logToDebate("12345")
        verify(view).openDebateScreen(token)
    }

    @Test
    fun shouldOpenDebateScreenWithRealAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        val token = AuthToken("authToken", "realUserId")
        forCodeReturnTokenFromRepo(debateCode = "23456", token = token)
        logToDebate("23456")
        verify(view).openDebateScreen(token)
    }

    @Test
    fun shouldShowLoaderOnLoggingWithTokenFromRepository() {
        forCodeReturnTokenFromRepo(debateCode = "23456", token = AuthToken("authToken", "userId"))
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
        forCodeReturnTokenFromRepo(debateCode = "23456", token = AuthToken("authToken", "userId"))
        logToDebate("23456")
        verify(view, never()).showWrongPinError()
    }

    @Test
    fun shouldUseGivenSchedulerForSubscribeOnInApiCall() {
        val subscribeOn = TestScheduler()
        val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.onLogToDebate("12345")
        returnTokenFromApi("authToken", "userId")
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerForObserveOnInApiCall() {
        val observeOn = TestScheduler()
        val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.onLogToDebate("12345")
        returnTokenFromApi("authToken", "userId")
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldSaveDebateCode() {
        logToDebate(debateCode = "12345")
        verify(debateRepo).saveLatestDebateCode("12345")
    }

    @Test
    fun shouldNotFillLatestDebateCodeWhenNotSaved() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn(null)
        controller.onCreate()
        verify(view, never()).fillDebateCode(any())
    }

    @Test
    fun shouldFillLatestDebateCodeWhenSaved() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("12345")
        controller.onCreate()
        verify(view).fillDebateCode("12345")
    }

    @Test
    fun shouldUseCorrectDebateCodeWhenCallingApi() {
        controller.onCreate()
        controller.onLogToDebate("12345")
        verify(loginApi).login("12345")
    }

    private fun forCodeReturnTokenFromRepo(debateCode: String, token: AuthToken) {
        whenever(debateRepo.hasToken(debateCode = debateCode)).thenReturn(true)
        whenever(debateRepo.getTokenForDebate(debateCode = debateCode)).thenReturn(token)
    }

    private fun returnTokenFromApi(token: String, userId: String) {
        apiSubject.onSuccess(AuthToken(token, userId))
    }

    private fun logToDebate(debateCode: String = "12345") {
        controller.onLogToDebate(debateCode)
    }
}
