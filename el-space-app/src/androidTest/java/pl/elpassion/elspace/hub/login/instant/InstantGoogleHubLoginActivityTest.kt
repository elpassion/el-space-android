package pl.elpassion.elspace.hub.login.instant

import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity

class InstantGoogleHubLoginActivityTest {

    val repository = mock<InstantGoogleHubLogin.Repository>()
    val openOnLoggedInScreen = mock<(Context) -> Unit>()

    @Rule @JvmField
    val rule = rule<InstantGoogleHubLoginActivity>(autoStart = false) {
        InstantGoogleHubLoginActivity.provideRepository = { repository }
        InstantGoogleHubLoginActivity.openOnLoggedInScreen = openOnLoggedInScreen
    }

    @Test
    fun shouldOpenOnLoggedInScreenIfUserIsLoggedInOnCreate() {
        whenever(repository.readToken()).thenReturn("token")
        rule.startActivity()
        verify(openOnLoggedInScreen).invoke(any())
    }
}