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
import pl.elpassion.elspace.common.isDisplayedEffectively
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.debate.AuthToken
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.details.DebateDetailsActivity

class DebateLoginActivityTest {

    private val tokenRepo = mock<DebatesRepository>()
    private val apiSubject = SingleSubject.create<AuthToken>()
    private val api = mock<DebateLogin.Api>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<DebateLoginActivity> {
        whenever(tokenRepo.hasToken(any())).thenReturn(false)
        DebatesRepositoryProvider.override = { tokenRepo }
        DebateLogin.ApiProvider.override = { api.apply { whenever(login(any())).thenReturn(apiSubject) } }
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        onId(R.id.toolbar)
                .isDisplayed()
                .hasChildWithText(R.string.debate_title)
    }

    @Test
    fun shouldShowToolbarWithBackArrow() {
        onToolbarBackArrow().isDisplayed()
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
    fun shouldHaveCorrectDebateCodeInput() {
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginPinInputText)
                .isDisplayed()
                .replaceText("123456")
                .hasText("12345")
                .check(matches(withInputType(TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_NORMAL)))
    }

    @Test
    fun shouldShowHintInLoginPinInputField() {
        onId(R.id.debateLoginPinInputText).textInputEditTextHasHint(R.string.debate_login_hint_pin)
    }

    @Test
    fun shouldUseCorrectDebateCodeOnLogin() {
        onId(R.id.debateLoginPinInputText)
                .click()
                .replaceText("12345")
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginButton).click()
        verify(api).login("12345")
    }

    @Test
    fun shouldCallApiWhenCodeKeyboardConfirmClick() {
        onId(R.id.debateLoginPinInputText)
                .click()
                .replaceText("12345")
                .pressImeActionButton()
        verify(api).login("12345")
    }

    @Test
    fun shouldCallApiWithRealDataWhenCodeKeyboardConfirmClick() {
        onId(R.id.debateLoginPinInputText)
                .click()
                .replaceText("56789")
                .pressImeActionButton()
        verify(api).login("56789")
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
        loginToDebate()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderOnStart() {
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowErrorOnLoginButtonClickIfDebateCodeIsIncorrect() {
        loginToDebate(debateCode = "12")
        onText(R.string.debate_login_code_incorrect).isDisplayed()
    }

    @Test
    fun shouldNotShowErrorOnStart() {
        onText(R.string.debate_login_code_incorrect).doesNotExist()
    }

    @Test
    fun shouldShowDebateDetailsOnLoginClick() {
        stubIntentAndRepo()
        loginToDebate()
        checkIntent(DebateDetailsActivity::class.java)
    }

    @Test
    fun shouldOpenDebateScreenWithTokenFromRepo() {
        stubIntentAndRepo()
        loginToDebate()
        intended(allOf(
                hasExtra("debateAuthTokenKey", AuthToken("tokenFromRepo", "userIdFromRepo")),
                hasComponent(DebateDetailsActivity::class.java.name)))
    }

    @Test
    fun shouldSaveTokenReturnedFromApiAndOpenDebateScreen() {
        stubAllIntents()
        loginToDebate(debateCode = "12345")
        apiSubject.onSuccess(AuthToken("authTokenFromApi", "userIdFromApi"))
        verify(tokenRepo).saveDebateToken(debateCode = "12345", authToken = AuthToken("authTokenFromApi", "userIdFromApi"))
        intended(allOf(
                hasExtra("debateAuthTokenKey", AuthToken("authTokenFromApi", "userIdFromApi")),
                hasComponent(DebateDetailsActivity::class.java.name)))
    }

    @Test
    fun shouldShowErrorOnLoginError() {
        loginToDebate()
        apiSubject.onError(RuntimeException())
        onText(R.string.debate_login_error).isDisplayedEffectively()
    }

    @Test
    fun shouldShowDebateClosedErrorOnLogin406CodeErrorFromApi() {
        loginToDebate()
        apiSubject.onError(createHttpException(406))
        onText(R.string.debate_login_debate_closed_error).isDisplayed()
    }

    @Test
    fun shouldCloseDebateClosedErrorDialogOnOkClick() {
        loginToDebate()
        apiSubject.onError(createHttpException(406))
        onText(R.string.debate_login_debate_closed_error_button_ok).click()
        onText(R.string.debate_login_debate_closed_error).doesNotExist()
    }

    private fun stubIntentAndRepo() {
        stubAllIntents()
        whenever(tokenRepo.hasToken("12345")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate("12345")).thenReturn(AuthToken("tokenFromRepo", "userIdFromRepo"))
    }

    private fun loginToDebate(debateCode: String = "12345") {
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginPinInputText).replaceText(debateCode)
        onId(R.id.debateLoginButton).click()
    }
}
