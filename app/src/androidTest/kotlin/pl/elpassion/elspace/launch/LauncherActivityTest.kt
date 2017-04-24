package pl.elpassion.elspace.launch

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.launcher.LauncherActivity

class LauncherActivityTest {

    @JvmField @Rule
    val rule = rule<LauncherActivity> {}

    @Test
    fun shouldShowDebateButton() {
        onText(R.string.open_debate).isDisplayed()
    }

    @Test
    fun shouldShowHubButton() {
        onText(R.string.open_hub).isDisplayed()
    }
}