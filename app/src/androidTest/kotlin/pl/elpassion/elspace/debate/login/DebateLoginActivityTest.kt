package pl.elpassion.elspace.debate.login

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
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.debate.DebateTokenRepository
import pl.elpassion.elspace.debate.DebateTokenRepositoryProvider
import pl.elpassion.elspace.debate.details.DebateScreen
import pl.elpassion.elspace.debate.login.DebateLogin.Api.LoginResponse
import rx.subjects.PublishSubject

class DebateLoginActivityTest {

    private val tokenRepo = mock<DebateTokenRepository>()
    private val apiSubject = PublishSubject.create<DebateLogin.Api.LoginResponse>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<DebateLoginActivity> {
        whenever(tokenRepo.hasToken(any())).thenReturn(false)
        DebateTokenRepositoryProvider.override = { tokenRepo }
        DebateLogin.ApiProvider.override = { mock<DebateLogin.Api>().apply { whenever(login(any())).thenReturn(apiSubject) } }
    }

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

    @Test
    fun shouldShowLoaderOnApiCall() {
        onId(R.id.debateCode).replaceText("12345")
        onId(R.id.loginButton).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderOnStart() {
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowErrorOnLoginButtonClickIfDebateCodeIsIncorrect() {
        onId(R.id.debateCode).replaceText("12")
        onId(R.id.loginButton).click()
        onText(R.string.debate_code_incorrect).isDisplayed()
    }

    @Test
    fun shouldNotShowErrorOnStart() {
        onText(R.string.debate_code_incorrect).doesNotExist()
    }

    @Test
    fun shouldOpenDebateScreenWithTokenFromRepo() {
        whenever(tokenRepo.hasToken("12345")).thenReturn(true)
        whenever(tokenRepo.getTokenForDebate("12345")).thenReturn("tokenFromRepo")
        onId(R.id.debateCode).replaceText("12345")
        onId(R.id.loginButton).click()
        intended(allOf(
                hasExtra("debateAuthTokenKey", "tokenFromRepo"),
                hasComponent(DebateScreen::class.java.name)))
    }

    @Test
    fun shouldSaveTokenReturnedFromApiAndOpenDebateScreen() {
        onId(R.id.debateCode).replaceText("12345")
        onId(R.id.loginButton).click()
        apiSubject.onNext(LoginResponse("authTokenFromApi"))
        verify(tokenRepo).saveDebateToken("12345", "authTokenFromApi")
        intended(allOf(
                hasExtra("debateAuthTokenKey", "authTokenFromApi"),
                hasComponent(DebateScreen::class.java.name)))
    }
    
    @Test
    fun shouldShowErrorWhenLoginFails() {
        onId(R.id.debateCode).replaceText("12345")
        onId(R.id.loginButton).click()
        apiSubject.onError(RuntimeException())
        Thread.sleep(50)
        onText(R.string.debate_login_fail).isDisplayed()
    }
}