package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import com.elpassion.android.commons.espresso.*
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
import pl.elpassion.elspace.debate.LoginCredentials
import pl.elpassion.elspace.debate.chat.DebateChatActivity
import java.lang.Thread.sleep

class DebateDetailsActivityTest : DebateDetailsScreenImpl() {

    private val debateDetailsSubject = SingleSubject.create<DebateData>()
    private val sendVoteSubject = CompletableSubject.create()
    private val apiMock by lazy {
        mock<DebateDetails.Api>().apply {
            whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject)
            whenever(vote(any(), any())).thenReturn(sendVoteSubject)
        }
    }

    @JvmField
    @Rule
    val intents = InitIntentsRule()

    @JvmField
    @Rule
    val rule = rule<DebateDetailsActivity>(autoStart = false)

    @Before
    fun setup() {
        DebateDetails.ApiProvider.override = { apiMock }
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        startActivity()
        toolbar
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
        debateInfoDescription.isDisplayed()
    }

    @Test
    fun shouldShowTopicReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        debateTopic
                .isDisplayed()
                .hasText("topic")
    }

    @Test
    fun shouldShowChooseSideInfo() {
        startActivity()
        debateInfoDetails.isDisplayed()
    }

    @Test
    fun shouldShowRememberInfo() {
        startActivity()
        debateInfoSubdetails.perform(scrollTo()).isDisplayed()
    }

    @Test
    fun shouldShowChatButton() {
        startActivity()
        debateChatButton
                .hasText(R.string.debate_details_button_chat)
                .isDisplayed()
                .isEnabled()
    }

    @Test
    fun shouldShowInactiveImagesInButtons() {
        startActivity()
        positiveAnswerImage.hasTag(equalTo(null))
        debateNegativeAnswerImage.hasTag(equalTo(null))
        neutralAnswerImage.hasTag(equalTo(null))
    }

    @Test
    fun shouldShowCorrectAnswersReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        debatePositiveAnswerText
                .isDisplayed()
                .hasText("answerPositive")
        debateNegativeAnswerText
                .isDisplayed()
                .hasText("answerNegative")
        debateNeutralAnswerText
                .isDisplayed()
                .hasText("answerNeutral")
    }

    @Test
    fun shouldHighlightPositiveAnswerWhenLastAnswerWasPositive() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 1))
        positiveAnswerImage.hasTag(equalTo(R.color.answerPositive))
        debateNegativeAnswerImage.hasTag(equalTo(R.color.answerInactive))
        neutralAnswerImage.hasTag(equalTo(R.color.answerInactive))
    }

    @Test
    fun shouldHighlightNegativeAnswerWhenLastAnswerWasNegative() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 2))
        positiveAnswerImage.hasTag(equalTo(R.color.answerInactive))
        debateNegativeAnswerImage.hasTag(equalTo(R.color.answerNegative))
        neutralAnswerImage.hasTag(equalTo(R.color.answerInactive))
    }

    @Test
    fun shouldHighlightNeutralAnswerWhenLastAnswerWasNeutral() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = 3))
        positiveAnswerImage.hasTag(equalTo(R.color.answerInactive))
        debateNegativeAnswerImage.hasTag(equalTo(R.color.answerInactive))
        neutralAnswerImage.hasTag(equalTo(R.color.answerNeutral))
    }

    @Test
    fun shouldNotHighlightAnswerWhenLastAnswerWasNull() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData(lastAnswerId = null))
        positiveAnswerImage.hasTag(equalTo(null))
        debateNegativeAnswerImage.hasTag(equalTo(null))
        neutralAnswerImage.hasTag(equalTo(null))
    }

    @Test
    fun shouldShowLoaderWhenCallingApi() {
        startActivity()
        debateMainLoader.isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        debateMainLoader.doesNotExist()
    }

    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFailed() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        errorWhileGettingDebateDetails.isDisplayedEffectively()
    }


    @Test
    fun shouldShowDebateClosedErrorOnGetDebateDetails403CodeErrorFromApi() {
        startActivity()
        debateDetailsSubject.onError(createHttpException(403))
        debateClosedView.isDisplayed()
    }

    @Test
    fun shouldShowRefreshButtonWithDebateDetailsError() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        refreshAfterErrorButton.isDisplayedEffectively()
    }

    @Test
    fun shouldCallApiSecondTimeOnRefreshClickedWhenPreviousCallFailed() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        sleep(200)
        refreshAfterErrorButton.click()
        verify(apiMock, times(2)).getDebateDetails(any())
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        positiveAnswerButton.perform(scrollTo()).click()
        positiveAnswerLoader.isDisplayed()
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.perform(scrollTo()).click()
        negativeAnswerLoader.isDisplayed()
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        neutralAnswerButton.perform(scrollTo()).click()
        neutralAnswerLoader.isDisplayed()
    }

    @Test
    fun shouldNotShowPositiveVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        positiveAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        positiveAnswerLoader.isNotDisplayed()
    }

    @Test
    fun shouldNotShowNegativeVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        negativeAnswerLoader.isNotDisplayed()
    }

    @Test
    fun shouldNotShowNeutralVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        neutralAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        neutralAnswerLoader.isNotDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnAnswerAndApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        voteSuccessfulText.isDisplayedEffectively()
    }

    @Test
    fun shouldShowDebateClosedErrorOnVote403CodeErrorFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        neutralAnswerButton.perform(scrollTo()).click()
        sendVoteSubject.onError(createHttpException(403))
        debateClosedView.isDisplayed()
    }

    @Test
    fun shouldShowSlowDownInformationOn429ErrorCodeFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.click()
        sendVoteSubject.onError(createHttpException(429))
        slowDownVotingText.isDisplayed()
        slowDownVotingInfo.isDisplayed()
    }

    @Test
    fun shouldCloseSlowDownInformationOnButtonClick() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.click()
        sendVoteSubject.onError(createHttpException(429))
        voteSlowDownOkButton.click()
        slowDownVotingText.doesNotExist()
        slowDownVotingInfo.doesNotExist()
    }

    @Test
    fun shouldShowVoteErrorWhenApiCallFails() {
        startActivityAndSuccessfullyReturnDebateDetails()
        neutralAnswerButton.perform(scrollTo()).click()
        sendVoteSubject.onError(RuntimeException())
        voteFailText.isDisplayedEffectively()
    }

    @Test
    fun shouldNotShowVoteErrorWhenApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        neutralAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        voteFailText.doesNotExist()
    }

    @Test
    fun shouldUseTokenPassedWithIntent() {
        startActivity("newToken")
        verify(apiMock).getDebateDetails("newToken")
    }

    @Test
    fun shouldUseTokenPassedWithIntentWhenSendingVote() {
        val token = "newToken"
        startActivityAndSuccessfullyReturnDebateDetails(token, createDebateData())
        positiveAnswerButton.perform(scrollTo()).click()
        verify(apiMock).vote(eq(token), any())
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingPositiveVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        positiveAnswerButton.perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.positive)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNegativeVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        negativeAnswerButton.perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.negative)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNeutralVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        neutralAnswerButton.perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.neutral)
    }

    @Test
    fun shouldOpenChatScreenWhenChatButtonClicked() {
        startActivityAndSuccessfullyReturnDebateDetails()
        stubAllIntents()
        debateChatButton.click()
        checkIntent(DebateChatActivity::class.java)
    }

    @Test
    fun shouldOpenChatScreenWithGivenToken() {
        val token = "someToken"
        startActivityAndSuccessfullyReturnDebateDetails(token = token)
        stubAllIntents()
        debateChatButton.click()
        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("debateLoginCredentialsKey", LoginCredentials(token, 111)),
                IntentMatchers.hasComponent(DebateChatActivity::class.java.name)))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        positiveAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        positiveAnswerImage.hasTag(equalTo(R.color.answerPositive))
        debateNegativeAnswerImage.hasTag(equalTo(R.color.answerInactive))
        neutralAnswerImage.hasTag(equalTo(R.color.answerInactive))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        positiveAnswerImage.hasTag(equalTo(R.color.answerInactive))
        debateNegativeAnswerImage.hasTag(equalTo(R.color.answerNegative))
        neutralAnswerImage.hasTag(equalTo(R.color.answerInactive))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        neutralAnswerButton.perform(scrollTo()).click()
        voteSuccessfully()
        positiveAnswerImage.hasTag(equalTo(R.color.answerInactive))
        debateNegativeAnswerImage.hasTag(equalTo(R.color.answerInactive))
        neutralAnswerImage.hasTag(equalTo(R.color.answerNeutral))
    }

    @Test
    fun shouldHaveDisabledButtonsOnVoteCall() {
        startActivityAndSuccessfullyReturnDebateDetails()
        negativeAnswerButton.click()
        positiveAnswerButton.isDisabled()
        negativeAnswerButton.isDisabled()
        neutralAnswerButton.isDisabled()
    }

    @Test
    fun shouldHaveEnabledButtonsOnVoteCallEnd() {
        startActivityAndSuccessfullyReturnDebateDetails()
        voteSuccessfully()
        negativeAnswerButton.click()
        positiveAnswerButton.isEnabled()
        negativeAnswerButton.isEnabled()
        neutralAnswerButton.isEnabled()
    }

    private fun startActivity(token: String = "token") {
        rule.startActivity(DebateDetailsActivity.intent(InstrumentationRegistry.getTargetContext(), LoginCredentials(token, 111)))
    }

    private fun voteSuccessfully() {
        sendVoteSubject.onComplete()
    }

    private fun startActivityAndSuccessfullyReturnDebateDetails(token: String = "token", debateData: DebateData = createDebateData()) {
        startActivity(token)
        debateDetailsSubject.onSuccess(debateData)
    }
}