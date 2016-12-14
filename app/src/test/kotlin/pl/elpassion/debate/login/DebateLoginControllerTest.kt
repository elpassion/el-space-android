package pl.elpassion.debate.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class DebateLoginControllerTest {

    @Test
    fun shouldShowLogToPreviousDebateViewIfTokenIsProvidedOnCreate() {
        val view = mock<DebateLogin.View>()
        DebateLoginController(view).onCreate()
        verify(view).showLogToPreviousDebateView()
    }

}

interface DebateLogin {
    interface View {
        fun showLogToPreviousDebateView()
    }
}

class DebateLoginController(private val view: DebateLogin.View) {
    fun onCreate() {
        view.showLogToPreviousDebateView()
    }
}
