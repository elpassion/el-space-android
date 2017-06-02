package pl.elpassion.elspace.debate.comment

import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.text.InputType
import com.elpassion.android.commons.espresso.*
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule

class DebateCommentActivityTest {

    @JvmField @Rule
    val rule = rule<DebateCommentActivity>()

    @Test
    fun shouldShowTitle() {
        onText(R.string.debate_comment_title).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectCommentInput() {
        onId(R.id.debateCommentInputText)
                .isDisplayed()
                .replaceText("mess")
                .hasText("mess")
                .check(ViewAssertions.matches(ViewMatchers.withInputType(InputType.TYPE_CLASS_TEXT)))
    }

    @Test
    fun shouldShowHintInInputField() {
        onId(R.id.debateCommentInputText).textInputEditTextHasHint(R.string.debate_comment_hint)
    }
}