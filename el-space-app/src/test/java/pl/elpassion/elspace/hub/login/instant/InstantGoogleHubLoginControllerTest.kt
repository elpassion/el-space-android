package pl.elpassion.elspace.hub.login.instant

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class InstantGoogleHubLoginControllerTest {

    val view = mock<InstantGoogleHubLogin.View>()
    val controller = InstantGoogleHubLoginController(view)

    @Test
    fun shouldOpenOnLoggedInScreenIfUserIsLoggedInOnCreate() {
        controller.onCreate()
        verify(view).openOnLoggedInScreen()
    }
}
