package pl.elpassion.elspace.debate.comment

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.CompletableSubject
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider

class DebateCommentActivityTest {

    private val debateRepo = mock<DebatesRepository>().apply {
        whenever(getLatestDebateNickname()).thenReturn("mrNick")
        whenever(areCredentialsMissing(any())).thenReturn(false)
    }
    private val sendCommentSubject = CompletableSubject.create()
    private val api = mock<DebateComment.Api>().apply {
        whenever(comment(any(), any(), any(), any())).thenReturn(sendCommentSubject)
    }

    @JvmField @Rule
    val rule = rule<DebateCommentActivity>(false) {
        DebatesRepositoryProvider.override = { debateRepo }
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
        verify(api).comment(eq("someToken"), eq("message"), any(), any())
    }

    @Test
    fun shouldUseCorrectTokenAndMessageOnSendClick() {
        startActivity(debateToken = "someToken")
        onId(R.id.debateCommentInputText)
                .replaceText("message")
        Espresso.closeSoftKeyboard()
        onId(R.id.debateCommentSendButton).click()
        verify(api).comment(eq("someToken"), eq("message"), any(), any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenInputIsEmptyOnSendComment() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("")
                .pressImeActionButton()
        onText(R.string.debate_comment_invalid_input_error).isDisplayed()
    }

    @Test
    fun shouldShowCorrectInputOverLimitErrorMessageWhenInputIsOverLimitOnSendComment() {
        startActivity()
        val maxMessageLength = 100
        val message = InstrumentationRegistry.getTargetContext().resources.getString(R.string.debate_comment_input_over_limit_error).format(maxMessageLength)
        onId(R.id.debateCommentInputText)
                .replaceText(createString(maxMessageLength + 1))
                .pressImeActionButton()
        onText(message).isDisplayed()
    }

    @Test
    fun shouldGetMaxMessageLengthFromResourcesWhenInputIsOverLimitOnSendComment() {
        startActivity()
        val maxMessageLength = InstrumentationRegistry.getTargetContext().resources.getInteger(R.integer.debate_comment_max_message_length)
        val message = InstrumentationRegistry.getTargetContext().resources.getString(R.string.debate_comment_input_over_limit_error).format(maxMessageLength)
        onId(R.id.debateCommentInputText)
                .replaceText(createString(maxMessageLength + 1))
                .pressImeActionButton()
        onText(message).isDisplayed()
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
        assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldCloseScreenOnSuccessfullySentComment() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .replaceText("message")
                .pressImeActionButton()
        sendCommentSubject.onComplete()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        assertTrue(rule.activity.isFinishing)
    }

    private fun startActivity(debateToken: String = "debateToken") {
        rule.startActivity(DebateCommentActivity.intent(InstrumentationRegistry.getTargetContext(), debateToken))
    }
}