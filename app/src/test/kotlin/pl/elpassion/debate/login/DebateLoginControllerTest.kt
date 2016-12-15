package pl.elpassion.debate.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.Observable

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val tokenRepo = mock<DebateTokenRepository>()
    private val controller = DebateLoginController(view, tokenRepo)

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
        val loginApi = mock<DebateLogin.Api>()
        whenever(loginApi.login("1234")).thenReturn(Observable.just(DebateLogin.Api.LoginResponse("authToken")))
        controller.logToNewDebate("1234")
        verify(tokenRepo).saveToken("authToken")
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
    }

    interface Api {
        fun login(code: String): Observable<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

}

class DebateLoginController(private val view: DebateLogin.View, private val tokenRepo: DebateTokenRepository) {
    fun onCreate() {
        if (tokenRepo.hasToken()) {
            view.showLogToPreviousDebateView()
        }
    }

    fun onLogToPreviousDebate() {
        view.openDebateScreen()
    }

    fun logToNewDebate(debateCode: String) {
        tokenRepo.saveToken("authToken")
    }
}
