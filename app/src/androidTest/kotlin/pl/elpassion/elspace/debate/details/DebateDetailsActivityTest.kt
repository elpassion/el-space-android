package pl.elpassion.elspace.debate.details

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onId
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule

class DebateDetailsActivityTest {

    @JvmField @Rule
    val rule = rule<DebateDetailsActivity> {}

    @Test
    fun shouldShowQuestionView() {
        onId(R.id.debateDetailsQuestion).isDisplayed()
    }

    @Test
    fun shouldShowPositiveAnswer() {
        onId(R.id.debateDetailsPositiveAnswer).isDisplayed()
    }

    @Test
    fun shouldShowNeutralAnswer() {
        onId(R.id.debateDetailsNeutralAnswer).isDisplayed()
    }

    @Test
    fun shouldShowNegativeAnswer() {
        onId(R.id.debateDetailsNegativeAnswer).isDisplayed()
    }
}

