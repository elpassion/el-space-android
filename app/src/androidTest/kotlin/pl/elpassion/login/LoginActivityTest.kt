package pl.elpassion.login

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R

class LoginActivityTest {

    @JvmField @Rule
    val rule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @Test
    fun shouldHaveLoginButton() {
        onId(R.id.loginButton).hasText(R.string.login_button)
    }
}

