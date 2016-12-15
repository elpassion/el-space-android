package pl.elpassion.debate.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class DebateLoginControllerTest {

    private val view = mock<DebateLogin.View>()
    private val tokenRepo = mock<DebateTokenRepository> { on { hasToken() }.thenReturn(false) }
    private val controller = DebateLoginController(view, tokenRepo)

    @Test
    fun shouldShowLogToPreviousDebateViewIfTokenIsProvidedOnCreate() {
        controller.onCreate()
        verify(view).showLogToPreviousDebateView()
    }

    @Test
    fun shouldNotShowLogToPreviousDebateViewIfTokenIsNotProvidedOnCreate() {
        whenever(tokenRepo.hasToken()).thenReturn(true)
        controller.onCreate()
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
