package pl.elpassion.debate.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Subscription

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
    fun shouldShowLogToPreviousDebateViewIfTokenIsProvidedOnCreate() {
        controller.onCreate()
        verify(view).showLogToPreviousDebateView()
    }

    @Test
    fun shouldNotShowLogToPreviousDebateViewIfTokenIsNotProvidedOnCreate() {
        whenever(tokenRepo.hasToken()).thenReturn(false)
        controller.onCreate()
        verify(view, never()).showLogToPreviousDebateView()
    }

    @Test
    fun shouldOpenDebateScreenOnLogToPreviousDebate() {
        controller.onLogToPreviousDebate()
        verify(view).openDebateScreen()
    }

    @Test
    fun shouldSaveReturnedTokenOnLogToNewDebate() {
        onLoginWithCodeReturnToken(code = "1234", token = "authToken")
        logToNewDebate(debateCode = "1234")
        verify(tokenRepo).saveToken("authToken")
    }

    @Test
    fun shouldReallySaveReturnedTokenOnLogToNewDebate() {
        onLoginWithCodeReturnToken(code = "12345", token = "realAuthToken")
        logToNewDebate(debateCode = "12345")
        verify(tokenRepo).saveToken("realAuthToken")
    }

    @Test
    fun shouldShowErrorIfLoginFails() {
        onLoginWithCodeReturnError(code = "error")
        controller.logToNewDebate("error")
        verify(view).showLoginFailedError()
    }

    @Test
    fun shouldNotShowErrorIfLoginSucceed() {
        onLoginWithCodeReturnToken(code = "12345")
        logToNewDebate(debateCode = "12345")
        verify(view, never()).showLoginFailedError()
    }

    @Test
    fun shouldShowLoaderOnLoginStart() {
        onLoginWithCodeReturnToken(code = "12345")
        logToNewDebate(debateCode = "12345")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnLoginEnd() {
        onLoginWithCodeReturnToken(code = "12345")
        logToNewDebate(debateCode = "12345")
        verify(view).hideLoader()
    }


    @Test
    fun shouldNotHideLoaderWhenLoginIsStillInProgress() {
        onLoginWithCodeReturnNever(code = "12345")
        logToNewDebate(debateCode = "12345")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfCallIsStillInProgress() {
        onLoginWithCodeReturnNever(code = "12345")
        logToNewDebate(debateCode = "12345")
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfCallIsNotInProgress() {
        controller.onDestroy()
        verify(view, never()).hideLoader()
    }

    private fun onLoginWithCodeReturnNever(code: String) {
        whenever(loginApi.login(code)).thenReturn(Observable.never())
    }

    private fun logToNewDebate(debateCode: String = "12345") {
        controller.logToNewDebate(debateCode)
    }

    private fun onLoginWithCodeReturnError(code: String) {
        whenever(loginApi.login(code)).thenReturn(Observable.error(RuntimeException()))
    }

    private fun onLoginWithCodeReturnToken(code: String, token: String = "authToken") {
        whenever(loginApi.login(code)).thenReturn(Observable.just(DebateLogin.Api.LoginResponse(token)))
    }

}

interface DebateTokenRepository {
    fun hasToken(): Boolean
    fun saveToken(authToken: String)
}

interface DebateLogin {
    interface View {
        fun showLogToPreviousDebateView()
        fun openDebateScreen()
        fun showLoginFailedError()
        fun showLoader()
        fun hideLoader()
    }

    interface Api {
        fun login(code: String): Observable<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

}

class DebateLoginController(private val view: DebateLogin.View, private val tokenRepo: DebateTokenRepository, private val loginApi: DebateLogin.Api) {

    var subscription : Subscription? = null

    fun onCreate() {
        if (tokenRepo.hasToken()) {
            view.showLogToPreviousDebateView()
        }
    }

    fun onLogToPreviousDebate() {
        view.openDebateScreen()
    }

    fun logToNewDebate(debateCode: String) {
        subscription = loginApi.login(debateCode)
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    tokenRepo.saveToken(it.authToken)
                }, {
                    view.showLoginFailedError()
                })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }
}
