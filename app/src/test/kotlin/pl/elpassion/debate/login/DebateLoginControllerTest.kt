package pl.elpassion.debate.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class DebateLoginControllerTest {

    @Test
    fun shouldShowLogToPreviousDebateViewIfTokenIsProvidedOnCreate() {
        val view = mock<DebateLogin.View>()
        val tokenRepo = mock<DebateTokenRepository> { on { hasToken() }.thenReturn(false) }
        DebateLoginController(view, tokenRepo).onCreate()
        verify(view).showLogToPreviousDebateView()
    }

    @Test
    fun shouldNotShowLogToPreviousDebateViewIfTokenIsNotProvidedOnCreate() {
        val view = mock<DebateLogin.View>()
        val tokenRepo = mock<DebateTokenRepository> { on { hasToken() }.thenReturn(true) }
        DebateLoginController(view, tokenRepo).onCreate()
        verify(view, never()).showLogToPreviousDebateView()
    }

}

interface DebateTokenRepository {
    fun hasToken(): Boolean
}

interface DebateLogin {
    interface View {
        fun showLogToPreviousDebateView()
    }
}

class DebateLoginController(private val view: DebateLogin.View, private val tokenRepo: DebateTokenRepository) {
    fun onCreate() {
        if (!tokenRepo.hasToken()) {
            view.showLogToPreviousDebateView()
        }
    }
}
