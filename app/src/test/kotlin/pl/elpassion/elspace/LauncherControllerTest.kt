package pl.elpassion.elspace

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.elspace.launcher.Launcher
import pl.elpassion.elspace.launcher.LauncherController

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
}
