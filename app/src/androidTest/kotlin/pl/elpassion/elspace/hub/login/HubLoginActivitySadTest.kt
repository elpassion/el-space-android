package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.closeSoftKeyboard
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.hub.report.list.ReportListActivity
import rx.Observable

class HubLoginActivitySadTest {

    val loginRepository = mock<HubLogin.Repository>().apply { whenever(readToken()).thenReturn(null) }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<HubLoginActivity> {
        HubLoginTokenApiProvider.override = {
            mock<HubLogin.TokenApi>().apply {
                whenever(loginWithGoogleToken(any())).thenReturn(Observable.never())
            }
        }
        HubLoginRepositoryProvider.override = { loginRepository }
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
        stubAllIntents()
        val token = "token"
        login(token)
        verify(loginRepository, times(1)).saveToken(token)
    }

    @Test
    fun shouldOpenReportListScreenWhenTokenIsProvidedAndLoginButtonIsClicked() {
        stubAllIntents()
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
        onId(R.id.loginCoordinator).check(matches(not(hasDescendant(withText(R.string.token_empty_error)))))
    }

    @Test
    fun shouldOpenHubWebsiteOnHub() {
        intending(anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))
        val hubTokenUri = getTargetContext().getString(R.string.hub_token_uri)
        onId(R.id.loginHubButton).click()
        intended(allOf(hasData(hubTokenUri), hasAction(ACTION_VIEW)))
    }

    private fun login(token: String) {
        onId(R.id.tokenInput).replaceText(token)
        closeSoftKeyboard()
        onId(R.id.loginButton).click()
    }
}

