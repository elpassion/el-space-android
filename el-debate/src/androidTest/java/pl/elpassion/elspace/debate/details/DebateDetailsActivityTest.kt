package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.elpassion.android.commons.espresso.*
import com.elpassion.android.commons.espresso.matchers.withParentId
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.hamcrest.Matchers
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.isDisplayedEffectively
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.debate.comment.DebateCommentActivity
import java.lang.Thread.sleep

class DebateDetailsActivityTest {

    private val debateDetailsSubject = SingleSubject.create<DebateData>()
    private val sendVoteSubject = CompletableSubject.create()
    private val apiMock by lazy {
        mock<DebateDetails.Api>().apply {
            whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject)
            whenever(vote(any(), any())).thenReturn(sendVoteSubject)
        }
    }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<DebateDetailsActivity>(autoStart = false)

    @Before
    fun setup() {
        DebateDetails.ApiProvider.override = { apiMock }
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        startActivity()
        onId(R.id.toolbar)
                .isDisplayed()
                .hasChildWithText(R.string.debate_title)
    }

    @Test
    fun shouldExitScreenOnBackArrowClick() {
        startActivity()
        onToolbarBackArrow().click()
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldShowTopicInfo() {
        startActivity()
        onText(R.string.debate_details_info_topic).isDisplayed()
    }

    @Test
    fun shouldShowTopicReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateTopic)
                .isDisplayed()
                .hasText("topic")
    }

    @Test
    fun shouldShowChooseSideInfo() {
        startActivity()
        onText(R.string.debate_details_info_choose_side).isDisplayed()
    }

    @Test
    fun shouldShowRememberInfo() {
        startActivity()
        onText(R.string.debate_details_info_remember).perform(scrollTo()).isDisplayed()
    }

    @Test
    fun shouldShowCommentButton() {
        startActivity()
        onId(R.id.debateCommentButton)
                .hasText(R.string.debate_details_button_comment)
                .isDisplayed()
                .isEnabled()
    }

    @Test
    fun shouldShowInactiveImagesInButtons() {
        startActivity()
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(null))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(null))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(null))
    }

    @Test
    fun shouldShowCorrectAnswersReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerText)
                .isDisplayed()
                .hasText("answerPositive")
        onId(R.id.debateNegativeAnswerText)
                .isDisplayed()
                .hasText("answerNegative")
        onId(R.id.debateNeutralAnswerText)
                .isDisplayed()
                .hasText("answerNeutral")
    }

    @Test
    fun shouldHighlightPositiveAnswerWhenLastAnswerWasPositive() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 1))
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(R.color.blueDebatePositive))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
    }

    @Test
    fun shouldHighlightNegativeAnswerWhenLastAnswerWasNegative() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 2))
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(R.color.redDebateNegative))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
    }

    @Test
    fun shouldHighlightNeutralAnswerWhenLastAnswerWasNeutral() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 3))
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(R.color.greyDebateNeutral))
    }

    @Test
    fun shouldNotHighlightAnswerWhenLastAnswerWasNull() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = null))
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(null))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(null))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(null))
    }

    @Test
    fun shouldShowLoaderWhenCallingApi() {
        startActivity()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowDebateClosedErrorOnDebateDetails406CodeErrorFromApi() {
        startActivity()
        debateDetailsSubject.onError(createHttpException(406))
        onId(R.id.debateClosedView).isDisplayed()
    }

    @Test
    fun shouldShowDebateClosedErrorOnVote406CodeErrorFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        sendVoteSubject.onError(createHttpException(406))
        onId(R.id.debateClosedView).isDisplayed()
    }

    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFailed() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        onText(R.string.debate_details_error).isDisplayedEffectively()
    }

    @Test
    fun shouldShowRefreshButtonWithDebateDetailsError() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        onText(R.string.debate_details_error_refresh).isDisplayedEffectively()
    }

    @Test
    fun shouldCallApiSecondTimeOnRefreshClickedWhenPreviousCallFailed() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        sleep(100)
        onText(R.string.debate_details_error_refresh).click()
        verify(apiMock, times(2)).getDebateDetails(any())
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debatePositiveAnswerLoader)).isDisplayed()
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debateNegativeAnswerLoader)).isDisplayed()
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debateNeutralAnswerLoader)).isDisplayed()
    }

    @Test
    fun shouldNotShowPositiveVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debatePositiveAnswerLoader)).isNotDisplayed()
    }

    @Test
    fun shouldNotShowNegativeVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debateNegativeAnswerLoader)).isNotDisplayed()
    }

    @Test
    fun shouldNotShowNeutralVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onView(withId(R.id.debateAnswerLoader).withParentId(R.id.debateNeutralAnswerLoader)).isNotDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnAnswerAndApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_success).isDisplayedEffectively()
    }

    @Test
    fun shouldShowVoteErrorWhenApiCallFails() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        sendVoteSubject.onError(RuntimeException())
        onText(R.string.debate_details_vote_error).isDisplayedEffectively()
    }

    @Test
    fun shouldNotShowVoteErrorWhenApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_error).doesNotExist()
    }

    @Test
    fun shouldUseTokenPassedWithIntent() {
        startActivity(token = "newToken")
        verify(apiMock).getDebateDetails("newToken")
    }

    @Test
    fun shouldUseTokenPassedWithIntentWhenSendingVote() {
        val token = "newToken"
        startActivityAndSuccessfullyReturnDebateDetails(token, createDebateData())
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote(eq(token), any())
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingPositiveVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.positive)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNegativeVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.negative)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNeutralVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.neutral)
    }

    @Test
    fun shouldOpenCommentScreenWhenCommentButtonClicked() {
        startActivityAndSuccessfullyReturnDebateDetails()
        stubAllIntents()
        onId(R.id.debateCommentButton).click()
        checkIntent(DebateCommentActivity::class.java)
    }

    @Test
    fun shouldOpenCommentScreenWithGivenToken() {
        val token = "someToken"
        startActivityAndSuccessfullyReturnDebateDetails(token = token)
        stubAllIntents()
        onId(R.id.debateCommentButton).click()
        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("debateAuthTokenKey", token),
                IntentMatchers.hasComponent(DebateCommentActivity::class.java.name)))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(R.color.blueDebatePositive))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(R.color.redDebateNegative))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        voteSuccessfully()
        onId(R.id.debatePositiveAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNegativeAnswerImage).hasTag(equalTo(R.color.greyDebateInactive))
        onId(R.id.debateNeutralAnswerImage).hasTag(equalTo(R.color.greyDebateNeutral))
    }

    @Test
    fun shouldShowSlowDownInformationOn429ErrorCodeFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).click()
        sendVoteSubject.onError(createHttpException(429))
        onText(R.string.debate_vote_slow_down_title).isDisplayed()
        onText(R.string.debate_vote_slow_down_info).isDisplayed()
    }

    @Test
    fun shouldCloseSlowDownInformationOnButtonClick() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).click()
        sendVoteSubject.onError(createHttpException(429))
        onText(R.string.debate_vote_slow_down_OK_button).click()
        onText(R.string.debate_vote_slow_down_title).doesNotExist()
        onText(R.string.debate_vote_slow_down_info).doesNotExist()
    }

    @Test
    fun shouldHaveDisabledButtonsOnVoteCall() {
        startActivityAndSuccessfullyReturnDebateDetails()
        onId(R.id.debateNegativeAnswerButton).click()
        onId(R.id.debatePositiveAnswerButton).isDisabled()
        onId(R.id.debateNegativeAnswerButton).isDisabled()
        onId(R.id.debateNeutralAnswerButton).isDisabled()
    }

    @Test
    fun shouldHaveEnabledButtonsOnVoteCallEnd() {
        startActivityAndSuccessfullyReturnDebateDetails()
        voteSuccessfully()
        onId(R.id.debateNegativeAnswerButton).click()
        onId(R.id.debatePositiveAnswerButton).isEnabled()
        onId(R.id.debateNegativeAnswerButton).isEnabled()
        onId(R.id.debateNeutralAnswerButton).isEnabled()
    }

    private fun startActivity(token: String = "token") {
        rule.startActivity(DebateDetailsActivity.intent(InstrumentationRegistry.getTargetContext(), token))
    }

    private fun voteSuccessfully() {
        sendVoteSubject.onComplete()
    }

    private fun startActivityAndSuccessfullyReturnDebateDetails(token: String = "token", debateData: DebateData = createDebateData()) {
        startActivity(token)
        debateDetailsSubject.onSuccess(debateData)
    }
}