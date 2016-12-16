package pl.elpassion.debate.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.debate.DebateTokenRepository
import rx.Observable

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val tokenRepo = mock<DebateTokenRepository>()
    private val loginApi = mock<DebateLogin.Api>()
    private val controller = DebateLoginController(view, tokenRepo, loginApi)

    @Before
    fun setUp() {
        whenever(tokenRepo.hasToken()).thenReturn(true)
    }

    @Test
    fun shouldSaveReturnedTokenOnLogToDebate() {
        onLoginWithCodeReturnToken(code = "1234", token = "authToken")
        logToDebate(debateCode = "1234")
        verify(tokenRepo).saveToken("authToken")
    }

    @Test
    fun shouldReallySaveReturnedTokenOnLogToDebate() {
        onLoginWithCodeReturnToken(code = "12345", token = "realAuthToken")
        logToDebate(debateCode = "12345")
        verify(tokenRepo).saveToken("realAuthToken")
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
        onLoginWithCodeReturnToken(code = "12345")
        logToDebate(debateCode = "12345")
        verify(view).openDebateScreen()
    }

    @Test
    fun shouldNotOpenDebateScreenOnLoginFailure() {
        onLoginWithCodeReturnError(code = "123")
        logToDebate(debateCode = "123")
        verify(view, never()).openDebateScreen()
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