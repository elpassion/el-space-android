package pl.elpassion.elspace.launch

import com.elpassion.android.commons.espresso.*
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.debate.login.DebateLoginActivity
import pl.elpassion.elspace.hub.login.instant.InstantGoogleHubLoginActivity
import pl.elpassion.elspace.launcher.LauncherActivity

class LauncherActivityTest {

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LauncherActivity> {}

    @Test
    fun shouldShowDebateButton() {
        onText(R.string.launcher_button_debate).isDisplayed()
    }

    @Test
    fun shouldShowHubButton() {
        onText(R.string.launcher_button_hub).isDisplayed()
    }

    @Test
    fun shouldShowDebateLoginScreenOnDebateClick() {
        stubAllIntents()
        onId(R.id.launcherDebateButton).click()
        checkIntent(DebateLoginActivity::class.java)
    }

    @Test
    fun shouldShowHubLoginScreenOnHubClick() {
        stubAllIntents()
        onId(R.id.launcherHubButton).click()
        checkIntent(InstantGoogleHubLoginActivity::class.java)
    }
}