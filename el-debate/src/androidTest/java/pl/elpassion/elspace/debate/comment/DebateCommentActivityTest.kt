package pl.elpassion.elspace.debate.comment

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.text.InputType
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule

class DebateCommentActivityTest {

    private val api = mock<DebateComment.Api>().apply {
        whenever(comment(any(), any())).thenReturn(Completable.never())
    }

    @JvmField @Rule
    val rule = rule<DebateCommentActivity>(false) {
        DebateComment.ApiProvider.override = { api }
    }

    @Test
    fun shouldHaveCorrectCommentInput() {
        startActivity()
        onId(R.id.debateCommentInputText)
                .isDisplayed()
                .replaceText("mess")
                .hasText("mess")
                .check(ViewAssertions.matches(ViewMatchers.withInputType(InputType.TYPE_CLASS_TEXT)))
    }

    @Test
    fun shouldShowHintInInputField() {
        startActivity()
        onId(R.id.debateCommentInputText).textInputEditTextHasHint(R.string.debate_comment_hint)
    }

    @Test
    fun shouldUseCorrectTokenAndMessageOnKeyboardConfirmClick() {
        startActivity(debateToken = "someToken")
        onId(R.id.debateCommentInputText)
                .typeText("message")
                .pressImeActionButton()
        verify(api).comment("someToken", "message")
    }

    private fun startActivity(debateToken: String = "debateToken") {
        rule.launchActivity(DebateCommentActivity.intent(InstrumentationRegistry.getTargetContext(), debateToken))
    }
}