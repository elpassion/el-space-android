package pl.elpassion.eldebate

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.doesNotExist
import com.elpassion.android.commons.espresso.onToolbarBackArrow
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class DebateLoginActivityWithoutLauncherTest {

    @JvmField @Rule
    val rule = ActivityTestRule<DebateLoginActivity>(DebateLoginActivity::class.java)

    @Test
    fun shouldShowToolbarWithoutBackArrow() {
        onToolbarBackArrow().doesNotExist()
    }
}