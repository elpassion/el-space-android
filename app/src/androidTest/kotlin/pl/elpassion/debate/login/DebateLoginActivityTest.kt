package pl.elpassion.debate.login

import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL
import com.elpassion.android.commons.espresso.*
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

    @Test
    fun shouldHaveInstructionsString() {
        onText(R.string.debate_login_instructions).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectDebateCodeInput() {
        onId(R.id.debateCode)
                .isDisplayed()
                .replaceText("123456")
                .hasText("12345")
                .check(matches(withInputType(TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_NORMAL)))
    }

    @Test
    fun shouldHaveLoginButton() {
        onId(R.id.loginButton)
                .hasText(R.string.login_button)
                .isDisplayed()
                .isEnabled()
    }
}

