package pl.elpassion.elspace.debate.login

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.LoginCredentials

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val debateRepo = mock<DebatesRepository>()
    private val loginApi = mock<DebateLogin.Api>()
    private val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))
    private val apiSubject = SingleSubject.create<LoginCredentials>()

    @Before
    fun setUp() {
        whenever(debateRepo.hasLoginCredentials(any())).thenReturn(false)
        whenever(loginApi.login(any())).thenReturn(apiSubject)
    }

    @Test
    fun shouldSaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12348")
        returnTokenFromApi("authToken", 222)
        verify(debateRepo).saveLoginCredentials(debateCode = "12348", loginCredentials = LoginCredentials("authToken", 222))
    }

    @Test
    fun shouldReallySaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("realAuthToken", 333)
        verify(debateRepo).saveLoginCredentials(debateCode = "12345", loginCredentials = LoginCredentials("realAuthToken", 333))
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
        returnTokenFromApi("authToken", 111)
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
        returnTokenFromApi("authToken", 111)
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
        returnTokenFromApi("authToken", 111)
        verify(view).openDebateScreen(LoginCredentials("authToken", 111))
    }

    @Test
    fun shouldNotOpenDebateScreenOnLoginFailure() {
        logToDebate(debateCode = "123")
        apiSubject.onError(RuntimeException())
        verify(view, never()).openDebateScreen(any())
    }

    @Test
    fun shouldOpenDebateScreenWithAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        val token = LoginCredentials("token", 111)
        forCodeReturnTokenFromRepo(debateCode = "12345", token = token)
        logToDebate("12345")
        verify(view).openDebateScreen(token)
    }

    @Test
    fun shouldOpenDebateScreenWithRealAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        val token = LoginCredentials("authToken", 777)
        forCodeReturnTokenFromRepo(debateCode = "23456", token = token)
        logToDebate("23456")
        verify(view).openDebateScreen(token)
    }

    @Test
    fun shouldShowLoaderOnLoggingWithTokenFromRepository() {
        forCodeReturnTokenFromRepo(debateCode = "23456", token = LoginCredentials("authToken", 111))
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
        forCodeReturnTokenFromRepo(debateCode = "23456", token = LoginCredentials("authToken", 111))
        logToDebate("23456")
        verify(view, never()).showWrongPinError()
    }

    @Test
    fun shouldUseGivenSchedulerForSubscribeOnInApiCall() {
        val subscribeOn = TestScheduler()
        val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.onLogToDebate("12345")
        returnTokenFromApi("authToken", 111)
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerForObserveOnInApiCall() {
        val observeOn = TestScheduler()
        val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.onLogToDebate("12345")
        returnTokenFromApi("authToken", 111)
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

    private fun forCodeReturnTokenFromRepo(debateCode: String, token: LoginCredentials) {
        whenever(debateRepo.hasLoginCredentials(debateCode = debateCode)).thenReturn(true)
        whenever(debateRepo.getLoginCredentialsForDebate(debateCode = debateCode)).thenReturn(token)
    }

    private fun returnTokenFromApi(token: String, userId: Long) {
        apiSubject.onSuccess(LoginCredentials(token, userId))
    }

    private fun logToDebate(debateCode: String = "12345") {
        controller.onLogToDebate(debateCode)
    }
}
