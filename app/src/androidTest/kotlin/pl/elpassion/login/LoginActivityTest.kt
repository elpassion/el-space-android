package pl.elpassion.login

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.InitIntentsRule
import pl.elpassion.common.checkIntent
import pl.elpassion.report.list.ReportListActivity

class LoginActivityTest {

    val loginRepository = mock<Login.Repository>().apply { whenever(readToken()).thenReturn(null) }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = object : ActivityTestRule<LoginActivity>(LoginActivity::class.java) {
        override fun beforeActivityLaunched() {
            LoginRepositoryProvider.override = { loginRepository }
        }
    }

    @Test
    fun shouldHaveLoginButton() {
        onId(R.id.loginButton).hasText(R.string.login_button)
    }

    @Test
    fun shouldHaveTokenEditText() {
        onId(R.id.tokenInput).isDisplayed()
    }

    @Test
    fun shouldSaveTokenWhenTokenIsProvidedAndLoginButtonIsPressed() {
        val token = "token"
        login(token)
        verify(loginRepository, times(1)).saveToken(token)
    }

    @Test
    fun shouldOpenReportListScreenWhenTokenIsProvidedAndLoginButtonIsClicked() {
        login("token")
        checkIntent(ReportListActivity::class.java)
    }

    @Test
    fun shouldShowErrorWhenProvidedTokenIsEmptyAndLoginButtonIsClicked() {
        login("")
        onText(R.string.token_empty_error).isDisplayed()
    }

    @Test
    fun shouldNotHaveErrorInfoOnStart() {
        onText(R.string.token_empty_error).isNotDisplayed()
    }

    private fun login(token: String) {
        onId(R.id.tokenInput).typeText(token)
        onId(R.id.loginButton).click()
    }
}

