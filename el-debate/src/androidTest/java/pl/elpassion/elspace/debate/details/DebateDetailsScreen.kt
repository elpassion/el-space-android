package pl.elpassion.elspace.debate.details

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.elpassion.android.commons.espresso.matchers.withParentId
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import pl.elpassion.R

open class DebateDetailsScreen {
    val debateChatButton = onId(R.id.debateChatButton)
    val toolbar = onId(R.id.toolbar)
    val debateTopic = onId(R.id.debateTopic)
    val debateInfoDetails = onText(R.string.debate_details_info_choose_side)
    val debateInfoSubdetails = onText(R.string.debate_details_info_remember)
    val debateMainLoader = onId(R.id.loader)
    val errorWhileGettingDebateDetails = onText(R.string.debate_details_error)
    val refreshAfterErrorButton = onText(R.string.debate_details_error_refresh)
    val debateClosedView = onId(R.id.debateClosedView)
    val voteSuccessfulText = onText(R.string.debate_details_vote_success)
    val debateInfoDescription = onText(R.string.debate_details_info_topic)
    val voteFailText = onText(R.string.debate_details_vote_error)

    val answers = AnswersView(
            positive = AnswersView.SingleAnswerView(
                    loader = onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debatePositiveAnswerLoader)),
                    button = onId(R.id.debatePositiveAnswerButton),
                    image = onId(R.id.debatePositiveAnswerImage),
                    text = onId(R.id.debatePositiveAnswerText)
            ),
            neutral = AnswersView.SingleAnswerView(
                    loader = onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debateNeutralAnswerLoader)),
                    button = onId(R.id.debateNeutralAnswerButton),
                    image = onId(R.id.debateNeutralAnswerImage),
                    text = onId(R.id.debateNeutralAnswerText)
            ),
            negative = AnswersView.SingleAnswerView(
                    loader = onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debateNegativeAnswerLoader)),
                    button = onId(R.id.debateNegativeAnswerButton),
                    image = onId(R.id.debateNegativeAnswerImage),
                    text = onId(R.id.debateNegativeAnswerText)
            )
    )
    val slowDownView = SlowDownView(
            title = onText(R.string.debate_details_vote_slow_down_title),
            info = onText(R.string.debate_details_vote_slow_down_info),
            button = onText(R.string.debate_details_vote_slow_down_OK_button))

    data class SlowDownView(val title: ViewInteraction,
                            val info: ViewInteraction,
                            val button: ViewInteraction)

    data class AnswersView(val positive: SingleAnswerView,
                           val negative: SingleAnswerView,
                           val neutral: SingleAnswerView) {
        operator fun invoke(function: AnswersView.() -> Unit) {
            function.invoke(this)
        }

        fun images(function: SingleAnswerViewPart.() -> Unit) {
            SingleAnswerViewPart(positive = positive.image, neutral = neutral.image, negative = negative.image).function()
        }

        fun buttons(function: SingleAnswerViewPart.() -> Unit) {
            SingleAnswerViewPart(positive = positive.button, negative = negative.button, neutral = neutral.button).function()
        }

        fun texts(function: SingleAnswerViewPart.() -> Unit) {
            SingleAnswerViewPart(positive = positive.text, negative = negative.text, neutral = neutral.text).function()
        }

        data class SingleAnswerView(val loader: ViewInteraction,
                                    val button: ViewInteraction,
                                    val image: ViewInteraction,
                                    val text: ViewInteraction) {
            operator fun invoke(function: SingleAnswerView.() -> Unit) {
                function.invoke(this)
            }
        }

        data class SingleAnswerViewPart(val positive: ViewInteraction,
                                        val negative: ViewInteraction,
                                        val neutral: ViewInteraction)
    }
}