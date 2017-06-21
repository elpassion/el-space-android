package pl.elpassion.elspace.debate.login

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.login.DebateLogin.Api.LoginResponse

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val debateRepo = mock<DebatesRepository>()
    private val loginApi = mock<DebateLogin.Api>()
    private val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))
    private val apiSubject = SingleSubject.create<DebateLogin.Api.LoginResponse>()

    @Before
    fun setUp() {
        whenever(debateRepo.hasToken(any())).thenReturn(false)
        whenever(loginApi.login(any(), any())).thenReturn(apiSubject)
    }

    @Test
    fun shouldSaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12348")
        returnTokenFromApi("authToken")
        verify(debateRepo).saveDebateToken(debateCode = "12348", authToken = "authToken")
    }

    @Test
    fun shouldReallySaveReturnedTokenAndDebateCodeOnLogToDebate() {
        logToDebate(debateCode = "12345")
        returnTokenFromApi("realAuthToken")
        verify(debateRepo).saveDebateToken(debateCode = "12345", authToken = "realAuthToken")
    }

    @Test
    fun shouldShowErrorIfLoginFails() {
        controller.onLogToDebate("error", "Gustaw333")
        apiSubject.onError(RuntimeException())
        verify(view).showLoginFailedError()
    }

    @Test
    fun shouldNotShowErrorIfLoginSucceed() {
        logToDebate()
        returnTokenFromApi("authToken")
        verify(view, never()).showLoginFailedError()
    }

    @Test
    fun shouldShowLoaderOnLoginStart() {
        logToDebate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnLoginEnd() {
        logToDebate()
        returnTokenFromApi("authToken")
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
    fun shouldShowWrongNicknameErrorWhenNicknameIsEmpty() {
        logToDebate("12345", "")
        verify(view).showWrongNicknameError()
    }

    @Test
    fun shouldUseGivenSchedulerForSubscribeOnInApiCall() {
        val subscribeOn = TestScheduler()
        val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.onLogToDebate("12345", "Gustaw333")
        returnTokenFromApi("authToken")
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerForObserveOnInApiCall() {
        val observeOn = TestScheduler()
        val controller = DebateLoginController(view, debateRepo, loginApi, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.onLogToDebate("12345", "Gustaw333")
        returnTokenFromApi("authToken")
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldSaveDebateCodeAndNickname() {
        controller.onLogToDebate("12345", "Wieslaw")
        verify(debateRepo).saveLatestDebateCode("12345")
        verify(debateRepo).saveLatestDebateNickname("Wieslaw")
    }

    @Test
    fun shouldNotFillLatestDebateCodeWhenNotSaved() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn(null)
        controller.onCreate()
        verify(view, never()).fillDebateCode(any())
    }

    @Test
    fun shouldNotFillLatestDebateNicknameWhenNotSaved() {
        whenever(debateRepo.getLatestDebateNickname()).thenReturn(null)
        controller.onCreate()
        verify(view, never()).fillDebateNickname(any())
    }

    @Test
    fun shouldFillLatestDebateCodeWhenSaved() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("12345")
        controller.onCreate()
        verify(view).fillDebateCode("12345")
    }

    @Test
    fun shouldFillLatestDebateNicknameWhenSaved() {
        whenever(debateRepo.getLatestDebateNickname()).thenReturn("Wieslaw")
        controller.onCreate()
        verify(view).fillDebateNickname("Wieslaw")
    }

    @Test
    fun shouldUseCorrectDebateCodeAndNicknameWhenCallingApi() {
        controller.onCreate()
        controller.onLogToDebate("12345", "Wieslaw")
        verify(loginApi).login("12345", "Wieslaw")
    }

    private fun forCodeReturnTokenFromRepo(debateCode: String, token: String) {
        whenever(debateRepo.hasToken(debateCode = debateCode)).thenReturn(true)
        whenever(debateRepo.getTokenForDebate(debateCode = debateCode)).thenReturn(token)
    }

    private fun returnTokenFromApi(token: String) {
        apiSubject.onSuccess(LoginResponse(token))
    }

    private fun logToDebate(debateCode: String = "12345", nickname: String = "Gustaw333") {
        controller.onLogToDebate(debateCode, nickname)
    }
}
