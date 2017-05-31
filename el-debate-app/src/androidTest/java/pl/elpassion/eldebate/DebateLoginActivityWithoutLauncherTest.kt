package pl.elpassion.eldebate

import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.doesNotExist
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class DebateLoginActivityWithoutLauncherTest {

    @JvmField @Rule
    val rule = ActivityTestRule<DebateLoginActivity>(DebateLoginActivity::class.java)

    @Test
    fun shouldShowToolbarWithoutBackArrow() {
        onToolbarBackArrow().doesNotExist()
    }

    fun onToolbarBackArrow() = Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
}