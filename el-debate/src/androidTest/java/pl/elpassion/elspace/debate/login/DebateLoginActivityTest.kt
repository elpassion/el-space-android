package pl.elpassion.elspace.debate.login

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.*
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
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.details.DebateDetailsActivity
import pl.elpassion.elspace.debate.login.DebateLogin.Api.LoginResponse

class DebateLoginActivityTest {

    private val tokenRepo = mock<DebatesRepository>()
    private val apiSubject = SingleSubject.create<DebateLogin.Api.LoginResponse>()
    private val api = mock<DebateLogin.Api>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<DebateLoginActivity> {
        whenever(tokenRepo.hasToken(any())).thenReturn(false)
        DebatesRepositoryProvider.override = { tokenRepo }
        DebateLogin.ApiProvider.override = { api.apply { whenever(login(any(), any())).thenReturn(apiSubject) } }
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
    fun shouldHaveCorrectNicknameInput() {
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginNicknameInputText)
                .isDisplayed()
                .replaceText("Alojzy666")
                .hasText("Alojzy666")
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldShowHintInLoginPinInputField() {
        onId(R.id.debateLoginPinInputText).textInputEditTextHasHint(R.string.debate_login_hint_pin)
    }

    @Test
    fun shouldShowHintInNicknameInputField() {
        onId(R.id.debateLoginNicknameInputText).textInputEditTextHasHint(R.string.debate_login_hint_nickname)
    }

    @Test
    fun shouldUseCorrectDebateCodeAndNicknameOnLogin() {
        onId(R.id.debateLoginPinInputText)
                .click()
                .replaceText("12345")
                .pressImeActionButton()
        onId(R.id.debateLoginNicknameInputText)
                .replaceText("Wieslaw")
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginButton).click()
        verify(api).login("12345", "Wieslaw")
    }

    @Test
    fun shouldCallApiWhenNicknameKeyboardConfirmClick() {
        onId(R.id.debateLoginPinInputText)
                .replaceText("12345")
        onId(R.id.debateLoginNicknameInputText)
                .click()
                .replaceText("Wieslaw")
                .pressImeActionButton()
        verify(api).login("12345", "Wieslaw")
    }

    @Test
    fun shouldCallApiWithRealDataWhenNicknameKeyboardConfirmClick() {
        onId(R.id.debateLoginPinInputText)
                .replaceText("56789")
        onId(R.id.debateLoginNicknameInputText)
                .click()
                .replaceText("Wieslaw666")
                .pressImeActionButton()
        verify(api).login("56789", "Wieslaw666")
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
    fun shouldShowErrorOnLoginButtonClickIfDebateNicknameIsIncorrect() {
        loginToDebate(nickname = "")
        onText(R.string.debate_login_nickname_incorrect).isDisplayed()
    }

    @Test
    fun shouldNotShowErrorOnStart() {
        onText(R.string.debate_login_code_incorrect).doesNotExist()
        onText(R.string.debate_login_nickname_incorrect).doesNotExist()
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
                hasExtra("debateAuthTokenKey", "tokenFromRepo"),
                hasComponent(DebateDetailsActivity::class.java.name)))
    }

    @Test
    fun shouldSaveTokenReturnedFromApiAndOpenDebateScreen() {
        stubAllIntents()
        loginToDebate(debateCode = "12345")
        apiSubject.onSuccess(LoginResponse("authTokenFromApi"))
        verify(tokenRepo).saveDebateToken(debateCode = "12345", authToken = "authTokenFromApi")
        intended(allOf(
                hasExtra("debateAuthTokenKey", "authTokenFromApi"),
                hasComponent(DebateDetailsActivity::class.java.name)))
    }

    @Test
    fun shouldShowErrorOnLoginError() {
        loginToDebate()
        apiSubject.onError(RuntimeException())
        onText(R.string.debate_login_error).isDisplayed()
    }

    @Test
    fun shouldShowDebateClosedErrorOnLogin404CodeErrorFromApi() {
        loginToDebate()
        apiSubject.onError(createHttpException(404))
        onText(R.string.debate_login_debate_closed_error).isDisplayed()
    }

    @Test
    fun shouldHaveOkButtonInDebateClosedErrorDialog() {
        loginToDebate()
        apiSubject.onError(createHttpException(404))
        onText(R.string.debate_login_debate_closed_error_button_ok).isDisplayed()
    }

    @Test
    fun shouldCloseDebateClosedErrorDialogOnOkClick() {
        loginToDebate()
        apiSubject.onError(createHttpException(404))
        onText(R.string.debate_login_debate_closed_error_button_ok).click()
        onText(R.string.debate_login_debate_closed_error).doesNotExist()
    }

    private fun stubIntentAndRepo() {
        stubAllIntents()
        whenever(tokenRepo.hasToken("12345")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate("12345")).thenReturn("tokenFromRepo")
    }

    private fun loginToDebate(debateCode: String = "12345", nickname: String = "SomeName") {
        Espresso.closeSoftKeyboard()
        onId(R.id.debateLoginPinInputText).replaceText(debateCode)
        onId(R.id.debateLoginNicknameInputText).replaceText(nickname)
        onId(R.id.debateLoginButton).click()
    }
}
