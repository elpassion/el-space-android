package pl.elpassion.login

import android.support.test.espresso.Espresso.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.replaceText
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
import pl.elpassion.common.rule
import pl.elpassion.report.list.ReportList
import pl.elpassion.report.list.ReportListActivity
import pl.elpassion.startActivity
import rx.Observable

class LoginActivitySadTest {

    val loginRepository = mock<Login.Repository>().apply { whenever(readToken()).thenReturn(null) }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LoginActivity>(autoStart = false) {
        ReportList.ServiceProvider.override = { mock<ReportList.Service>().apply { whenever(getReports()).thenReturn(Observable.just(emptyList())) } }
        LoginRepositoryProvider.override = { loginRepository }
    }

    @Test
    fun shouldHaveLoginButton() {
        rule.startActivity()
        onId(R.id.loginButton).hasText(R.string.login_button)
    }

    @Test
    fun shouldHaveTokenEditText() {
        rule.startActivity()
        onId(R.id.tokenInput).isDisplayed()
    }

    @Test
    fun shouldSaveTokenWhenTokenIsProvidedAndLoginButtonIsPressed() {
        rule.startActivity()
        val token = "token"
        login(token)
        verify(loginRepository, times(1)).saveToken(token)
    }

    @Test
    fun shouldOpenReportListScreenWhenTokenIsProvidedAndLoginButtonIsClicked() {
        rule.startActivity()
        login("token")
        checkIntent(ReportListActivity::class.java)
    }

    @Test
    fun shouldShowErrorWhenProvidedTokenIsEmptyAndLoginButtonIsClicked() {
        rule.startActivity()
        login("")
        onText(R.string.token_empty_error).isDisplayed()
    }

    @Test
    fun shouldNotHaveErrorInfoOnStart() {
        rule.startActivity()
        onText(R.string.token_empty_error).isNotDisplayed()
    }

    private fun login(token: String) {
        onId(R.id.tokenInput).perform(replaceText(token))
        closeSoftKeyboard()
        onId(R.id.loginButton).click()
    }
}

