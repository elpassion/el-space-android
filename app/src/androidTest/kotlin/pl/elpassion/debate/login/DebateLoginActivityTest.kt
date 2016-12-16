package pl.elpassion.debate.login

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule

class DebateLoginActivityTest {

    @JvmField @Rule
    val rule = rule<DebateLoginActivity>()

    @Test
    fun shouldHaveWelcomeString() {
        onText(R.string.debate_login_welcome).isDisplayed()
    }
}

