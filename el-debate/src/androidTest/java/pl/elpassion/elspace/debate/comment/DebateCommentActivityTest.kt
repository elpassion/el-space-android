package pl.elpassion.elspace.debate.comment

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
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
}