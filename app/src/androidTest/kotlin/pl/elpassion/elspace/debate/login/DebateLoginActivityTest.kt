package pl.elpassion.elspace.debate.login

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.subjects.SingleSubject
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.onToolbarBackArrow
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.debate.DebateTokenRepository
import pl.elpassion.elspace.debate.DebateTokenRepositoryProvider
import pl.elpassion.elspace.debate.details.DebateDetailsActivity
import pl.elpassion.elspace.debate.login.DebateLogin.Api.LoginResponse

class DebateLoginActivityTest {

    private val tokenRepo = mock<DebateTokenRepository>()
    private val apiSubject = SingleSubject.create<DebateLogin.Api.LoginResponse>()
    private val api = mock<DebateLogin.Api>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<DebateLoginActivity> {
        whenever(tokenRepo.hasToken(any())).thenReturn(false)
        DebateTokenRepositoryProvider.override = { tokenRepo }
        DebateLogin.ApiProvider.override = { api.apply { whenever(login(any())).thenReturn(apiSubject) } }
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        onId(R.id.toolbar)
                .isDisplayed()
                .hasChildWithText(R.string.debate_title)
    }

    @Test
    fun shouldExitScreenOnBackArrowClick() {
        onToolbarBackArrow().click()
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldHaveWelcomeString() {
        onText(R.string.debate_login_welcome).isDisplayed()
    }

    @Test
    fun shouldHaveInstructionsString() {
        onId(R.id.debateLoginInputText).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectDebateCodeInput() {
        onId(R.id.debateLoginInputText)
                .isDisplayed()
                .replaceText("123456")
                .hasText("12345")
                .check(matches(withInputType(TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_NORMAL)))
    }

    @Test
    fun shouldHaveLoginButton() {
        onId(R.id.debateLoginButton)
                .hasText(R.string.debate_login_button_login)
                .isDisplayed()
                .isEnabled()
    }

    @Test
    fun shouldShowLoaderOnApiCall() {
        loginToDebate("12345")
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderOnStart() {
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowErrorOnLoginButtonClickIfDebateCodeIsIncorrect() {
        loginToDebate("12")
        onText(R.string.debate_login_code_incorrect).isDisplayed()
    }

    @Test
    fun shouldNotShowErrorOnStart() {
        onText(R.string.debate_login_code_incorrect).doesNotExist()
    }

    @Test
    fun shouldShowDebateDetailsOnLoginClick() {
        stubIntentAndRepo()
        loginToDebate("12345")
        checkIntent(DebateDetailsActivity::class.java)
    }

    @Test
    fun shouldPerformConfirmActionOnKeyboardConfirmClick() {
        onId(R.id.debateLoginInputText).replaceText("12345")
        onId(R.id.debateLoginInputText).pressImeActionButton()
        verify(api).login(any())
    }

    @Test
    fun shouldOpenDebateScreenWithTokenFromRepo() {
        stubIntentAndRepo()
        loginToDebate("12345")
        intended(allOf(
                hasExtra("debateAuthTokenKey", "tokenFromRepo"),
                hasComponent(DebateDetailsActivity::class.java.name)))
    }

    @Test
    fun shouldSaveTokenReturnedFromApiAndOpenDebateScreen() {
        stubAllIntents()
        loginToDebate("12345")
        apiSubject.onSuccess(LoginResponse("authTokenFromApi"))
        verify(tokenRepo).saveDebateToken("12345", "authTokenFromApi")
        intended(allOf(
                hasExtra("debateAuthTokenKey", "authTokenFromApi"),
                hasComponent(DebateDetailsActivity::class.java.name)))
    }

    @Test
    fun shouldShowErrorWhenLoginFails() {
        apiSubject.onError(RuntimeException())
        loginToDebate("12345")
        onText(R.string.debate_login_fail).isDisplayed()
    }

    private fun stubIntentAndRepo() {
        stubAllIntents()
        whenever(tokenRepo.hasToken("12345")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate("12345")).thenReturn("tokenFromRepo")
    }

    private fun loginToDebate(debateCode: String) {
        onId(R.id.debateLoginInputText).replaceText(debateCode)
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginButton).click()
    }
}
