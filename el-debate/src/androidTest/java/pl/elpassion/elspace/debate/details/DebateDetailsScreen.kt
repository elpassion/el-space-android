package pl.elpassion.elspace.debate.details

import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import com.elpassion.android.commons.espresso.matchers.withParentId
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import pl.elpassion.R

open class DebateDetailsScreenImpl {
    val debateChatButton = onId(R.id.debateChatButton)
    val positiveAnswerImage= onId(R.id.debatePositiveAnswerImage)
    val debateNegativeAnswerImage = onId(R.id.debateNegativeAnswerImage)
    val neutralAnswerImage= onId(R.id.debateNeutralAnswerImage)
    val positiveAnswerButton = onId(R.id.debatePositiveAnswerButton)
    val negativeAnswerButton = onId(R.id.debateNegativeAnswerButton)
    val positiveAnswerLoader = Espresso.onView(ViewMatchers.withId(R.id.debateAnswerLoader).withParentId(R.id.debatePositiveAnswerLoader))
    val toolbar = onId(R.id.toolbar)
    val debateTopic = onId(R.id.debateTopic)
    val debateInfoDetails = onText(R.string.debate_details_info_choose_side)
    val debateInfoSubdetails = onText(R.string.debate_details_info_remember)
    val debatePositiveAnswerText = onId(R.id.debatePositiveAnswerText)
    val debateNegativeAnswerText = onId(R.id.debateNegativeAnswerText)
    val debateNeutralAnswerText = onId(R.id.debateNeutralAnswerText)
    val debateMainLoader = onId(R.id.loader)
    val errorWhileGettingDebateDetails = onText(R.string.debate_details_error)
    val refreshAfterErrorButton = onText(R.string.debate_details_error_refresh)
    val debateClosedView = onId(R.id.debateClosedView)
    val negativeAnswerLoader = Espresso.onView(ViewMatchers.withId(R.id.debateAnswerLoader).withParentId(R.id.debateNegativeAnswerLoader))
    val neutralAnswerButton = onId(R.id.debateNeutralAnswerButton)
    val neutralAnswerLoader = Espresso.onView(ViewMatchers.withId(R.id.debateAnswerLoader).withParentId(R.id.debateNeutralAnswerLoader))
    val voteSuccessfulText = onText(R.string.debate_details_vote_success)
    val debateInfoDescription = onText(R.string.debate_details_info_topic)
    val voteFailText = onText(R.string.debate_details_vote_error)
    val slowDownVotingText = onText(R.string.debate_details_vote_slow_down_title)
    val slowDownVotingInfo = onText(R.string.debate_details_vote_slow_down_info)
    val voteSlowDownOkButton = onText(R.string.debate_details_vote_slow_down_OK_button)
}