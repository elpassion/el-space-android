package pl.elpassion.elspace.debate.login

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.debate.DebateTokenRepository
import io.reactivex.Observable

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val tokenRepo = mock<DebateTokenRepository>()
    private val loginApi = mock<DebateLogin.Api>()
    private val controller = DebateLoginController(view, tokenRepo, loginApi)

    @Before
    fun setUp() {
        whenever(tokenRepo.hasToken(any())).thenReturn(false)
    }

    @Test
    fun shouldSaveReturnedTokenAndDebateCodeOnLogToDebate() {
        onLoginWithCodeReturnToken(code = "12348", token = "authToken")
        logToDebate(debateCode = "12348")
        verify(tokenRepo).saveDebateToken(debateCode = "12348", authToken = "authToken")
    }

    @Test
    fun shouldReallySaveReturnedTokenAndDebateCodeOnLogToDebate() {
        onLoginWithCodeReturnToken(code = "12345", token = "realAuthToken")
        logToDebate(debateCode = "12345")
        verify(tokenRepo).saveDebateToken(debateCode = "12345", authToken = "realAuthToken")
    }

    @Test
    fun shouldShowErrorIfLoginFails() {
        onLoginWithCodeReturnError(code = "error")
        controller.onLogToDebate("error")
        verify(view).showLoginFailedError()
    }

    @Test
    fun shouldNotShowErrorIfLoginSucceed() {
        onLoginWithCodeReturnToken(code = "12345")
        logToDebate(debateCode = "12345")
        verify(view, never()).showLoginFailedError()
    }

    @Test
    fun shouldShowLoaderOnLoginStart() {
        onLoginWithCodeReturnToken(code = "12345")
        logToDebate(debateCode = "12345")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnLoginEnd() {
        onLoginWithCodeReturnToken(code = "12345")
        logToDebate(debateCode = "12345")
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderWhenLoginIsStillInProgress() {
        onLoginWithCodeReturnNever(code = "12345")
        logToDebate(debateCode = "12345")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfCallIsStillInProgress() {
        onLoginWithCodeReturnNever(code = "12345")
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
        onLoginWithCodeReturnToken(code = "12345", token = "authToken")
        logToDebate(debateCode = "12345")
        verify(view).openDebateScreen("authToken")
    }

    @Test
    fun shouldNotOpenDebateScreenOnLoginFailure() {
        onLoginWithCodeReturnError(code = "123")
        logToDebate(debateCode = "123")
        verify(view, never()).openDebateScreen(any())
    }

    @Test
    fun shouldOpenDebateScreenWithAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        whenever(tokenRepo.hasToken(debateCode = "12345")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate(debateCode = "12345")).thenReturn("token")
        logToDebate("12345")
        verify(view).openDebateScreen("token")
    }

    @Test
    fun shouldOpenDebateScreenWithRealAuthTokenFromRepositoryIfAlreadyLoggedInOnLogin() {
        whenever(tokenRepo.hasToken(debateCode = "23456")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate(debateCode = "23456")).thenReturn("authToken")
        logToDebate("23456")
        verify(view).openDebateScreen("authToken")
    }

    @Test
    fun shouldShowLoaderOnLoggingWithTokenFromRepository() {
        whenever(tokenRepo.hasToken(debateCode = "23456")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate(debateCode = "23456")).thenReturn("authToken")
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
        whenever(tokenRepo.hasToken(debateCode = "23456")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate(debateCode = "23456")).thenReturn("authToken")
        logToDebate("23456")
        verify(view, never()).showWrongPinError()
    }

    private fun onLoginWithCodeReturnNever(code: String) {
        whenever(loginApi.login(code)).thenReturn(Observable.never())
    }

    private fun logToDebate(debateCode: String = "12345") {
        controller.onLogToDebate(debateCode)
    }

    private fun onLoginWithCodeReturnError(code: String) {
        whenever(loginApi.login(code)).thenReturn(Observable.error(RuntimeException()))
    }

    private fun onLoginWithCodeReturnToken(code: String, token: String = "authToken") {
        whenever(loginApi.login(code)).thenReturn(Observable.just(DebateLogin.Api.LoginResponse(token)))
    }

}