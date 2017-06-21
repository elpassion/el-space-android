package pl.elpassion.elspace.debate.comment

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.subjects.CompletableSubject
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import java.lang.Thread.sleep

class DebateCommentActivityTest {

    private val sendCommentSubject = CompletableSubject.create()
    private val api = mock<DebateComment.Api>().apply {
        whenever(comment(any(), any(), any())).thenReturn(sendCommentSubject)
    }

    @JvmField @Rule
    val rule = rule<DebateCommentActivity>(false) {
        DebateComment.ApiProvider.override = { api }
    }

    @Test
    fun shouldShowHintInInputField() {
        startActivity()
        onText(R.string.debate_comment_hint).isDisplayed()
    }

    @Test
    fun shouldShowCancelButton() {
        startActivity()
        onId(R.id.debateCommentCancelButton)
                .isDisplayed()
                .hasText(R.string.debate_comment_button_cancel)
    }

    @Test
    fun shouldShowSendButton() {
        startActivity()
        onId(R.id.debateCommentSendButton)
                .isDisplayed()
                .hasText(R.string.debate_comment_button_send)
    }

    @Test
    fun shouldHaveCorrectCommentInput() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .isDisplayed()
                .replaceText("mess")
                .hasText("mess")
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldUseCorrectTokenAndMessageOnKeyboardConfirmClick() {
        startActivity(debateToken = "someToken")
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        verify(api).comment("someToken", "message", "nickname")
    }

    @Test
    fun shouldUseCorrectTokenAndMessageOnSendClick() {
        startActivity(debateToken = "someToken")
        onId(R.id.debateCommentInputText)
                .replaceText("message")
        Espresso.closeSoftKeyboard()
        onId(R.id.debateCommentSendButton).click()
        verify(api).comment("someToken", "message", "nickname")
    }

    @Test
    fun shouldShowInvalidInputErrorWhenInputIsEmptyOnSendComment() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("")
                .pressImeActionButton()
        sleep(100)
        onText(R.string.debate_comment_invalid_input_error).isDisplayed()
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentFailed() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        sendCommentSubject.onError(RuntimeException())
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowSendCommentErrorWhenSendCommentFailed() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        sendCommentSubject.onError(RuntimeException())
        sleep(100)
        onText(R.string.debate_comment_send_error).isDisplayed()
    }


    @Test
    fun shouldNotClearInputWhenSendCommentFailed() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        sendCommentSubject.onError(RuntimeException())
        onId(R.id.debateCommentInputText).hasText("message")
    }

    @Test
    fun shouldCloseScreenOnCancelClick() {
        startActivity()
        onId(R.id.debateCommentCancelButton).click()
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldCloseScreenOnSuccessfullySentComment() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        sendCommentSubject.onComplete()
        sleep(100)
        Assert.assertTrue(rule.activity.isFinishing)
    }

    private fun startActivity(debateToken: String = "debateToken") {
        rule.launchActivity(DebateCommentActivity.intent(InstrumentationRegistry.getTargetContext(), debateToken))
    }
}