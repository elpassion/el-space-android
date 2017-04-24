package pl.elpassion.elspace

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class LauncherControllerTest {

    private val view = mock<Launcher.View>()
    private val controller = LauncherController(view)

    @Test
    fun shouldOpenDebateScreenOnDebate() {
        controller.onDebate()
        verify(view).openDebateLoginScreen()
    }

    @Test
    fun shouldOpenHubLoginScreenOnHub() {
        controller.onHub()
        verify(view).openHubLoginScreen()
    }

    interface Launcher {
        interface View {
            fun openDebateLoginScreen()
            fun openHubLoginScreen()
        }
    }

    class LauncherController(private val view: Launcher.View) {
        fun onDebate() {
            view.openDebateLoginScreen()
        }

        fun onHub() {
            view.openHubLoginScreen()
        }
    }
}
