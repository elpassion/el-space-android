package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.eq
import org.hamcrest.Matchers
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

class DebateDetailsActivityTest : DebateDetailsApiInteractor() {

    @JvmField
    @Rule
    val intents = InitIntentsRule()

    @JvmField
    @Rule
    val rule = rule<DebateDetailsActivity>(autoStart = false)
    
    @Before
    fun setup() {
        inject()
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        startActivity()
        toolbarHasText(R.string.debate_title)
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
        answers.images {
            positive.isNothighlighted()
            negative.isNothighlighted()
            neutral.isNothighlighted()
        }
    }

    @Test
    fun shouldShowCorrectAnswersReturnedFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.texts {
            positive.hasText("answerPositive")
            negative.hasText("answerNegative")
            neutral.hasText("answerNeutral")
        }
    }

    @Test
    fun shouldHighlightPositiveAnswerWhenLastAnswerWasPositive() {
        startActivity()
        debateApi.success(createDebateData(lastAnswerId = 1))
        answers.images {
            positive.hasPositiveColor()
            negative.hasInactiveColor()
            neutral.hasInactiveColor()
        }
    }

    @Test
    fun shouldHighlightNegativeAnswerWhenLastAnswerWasNegative() {
        startActivity()
        debateApi.success(createDebateData(lastAnswerId = 2))
        answers.images {
            positive.hasInactiveColor()
            negative.hasNegativeColor()
            neutral.hasInactiveColor()
        }
    }

    @Test
    fun shouldHighlightNeutralAnswerWhenLastAnswerWasNeutral() {
        startActivity()
        debateApi.success(createDebateData(lastAnswerId = 3))
        answers.images {
            positive.hasInactiveColor()
            negative.hasInactiveColor()
            neutral.hasNeutralColor()
        }
    }

    @Test
    fun shouldNotHighlightAnswerWhenLastAnswerWasNull() {
        startActivity()
        debateApi.success(createDebateData(lastAnswerId = null))
        answers.images {
            positive.isNothighlighted()
            negative.isNothighlighted()
            neutral.isNothighlighted()
        }
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
        debateApi.error(RuntimeException())
        errorWhileGettingDebateDetails.isDisplayedEffectively()
    }

    @Test
    fun shouldShowDebateClosedErrorOnGetDebateDetails403CodeErrorFromApi() {
        startActivity()
        debateApi.error(createHttpException(403))
        debateClosedView.isDisplayed()
    }

    @Test
    fun shouldShowRefreshButtonWithDebateDetailsError() {
        startActivity()
        debateApi.error(RuntimeException())
        refreshAfterErrorButton.isDisplayedEffectively()
    }

    @Test
    fun shouldCallApiSecondTimeOnRefreshClickedWhenPreviousCallFailed() {
        startActivity()
        debateApi.error(RuntimeException())
        sleep(200)
        debateApi.resetInvocations()
        refreshAfterErrorButton.click()
        debateApi.assertCalled()
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.positive {
            button.scrollAndClick()
            loader.isDisplayed()
        }
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.negative {
            button.scrollAndClick()
            loader.isDisplayed()
        }
    }

    @Test
    fun shouldShowVoteLoaderWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.neutral {
            button.scrollAndClick()
            loader.isDisplayed()
        }
    }

    @Test
    fun shouldNotShowPositiveVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.positive {
            button.scrollAndClick()
            votingApi.success()
            loader.isNotDisplayed()
        }
    }

    @Test
    fun shouldNotShowNegativeVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.negative {
            button.scrollAndClick()
            votingApi.success()
            loader.isNotDisplayed()
        }
    }

    @Test
    fun shouldNotShowNeutralVoteLoaderWhenApiCallFinished() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.neutral {
            button.scrollAndClick()
            votingApi.success()
            loader.isNotDisplayed()
        }
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnAnswerAndApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.negative.button.scrollAndClick()
        votingApi.success()
        voteSuccessfulText.isDisplayedEffectively()
    }

    @Test
    fun shouldShowDebateClosedErrorOnVote403CodeErrorFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.neutral.button.scrollAndClick()
        votingApi.error(createHttpException(403))
        debateClosedView.isDisplayed()
    }

    @Test
    fun shouldShowSlowDownInformationOn429ErrorCodeFromApi() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.negative.button.click()
        votingApi.error(createHttpException(429))
        slowDownInfoIsDisplayed()
    }

    @Test
    fun shouldCloseSlowDownInformationOnButtonClick() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.negative.button.click()
        votingApi.error(createHttpException(429))
        slowDownView.button.click()
        slowDownInfoIsNotDisplayed()
    }

    @Test
    fun shouldShowVoteErrorWhenApiCallFails() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.neutral.button.scrollAndClick()
        votingApi.error(RuntimeException())
        voteFailText.isDisplayedEffectively()
    }

    @Test
    fun shouldNotShowVoteErrorWhenApiCallFinishedSuccessfully() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers.neutral.button.scrollAndClick()
        votingApi.success()
        voteFailText.doesNotExist()
    }

    @Test
    fun shouldUseTokenPassedWithIntent() {
        startActivity("newToken")
        debateApi.assertCalled(tokenMatcher = { "newToken" })
    }

    @Test
    fun shouldUseTokenPassedWithIntentWhenSendingVote() {
        val token = "newToken"
        startActivityAndSuccessfullyReturnDebateDetails(token, createDebateData())
        answers.positive.button.scrollAndClick()
        votingApi.assertCalled(tokenMatcher = { eq(token) })
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingPositiveVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        answers.positive.button.scrollAndClick()
        votingApi.assertCalled(answerMatcher = { eq(debateData.answers.positive) })
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNegativeVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        answers.negative.button.scrollAndClick()
        votingApi.assertCalled(answerMatcher = { eq(debateData.answers.negative) })
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNeutralVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData = debateData)
        answers.neutral.button.scrollAndClick()
        votingApi.assertCalled(answerMatcher = { eq(debateData.answers.neutral) })
    }

    @Test
    fun shouldOpenChatScreenWhenChatButtonClicked() {
        startActivityAndSuccessfullyReturnDebateDetails()
        stubAllIntents()
        clickChatButton()
        checkIntent(DebateChatActivity::class.java)
    }

    @Test
    fun shouldOpenChatScreenWithGivenToken() {
        val token = "someToken"
        startActivityAndSuccessfullyReturnDebateDetails(token = token)
        stubAllIntents()
        clickChatButton()
        Intents.intended(Matchers.allOf(
                IntentMatchers.hasExtra("debateLoginCredentialsKey", LoginCredentials(token, 111)),
                IntentMatchers.hasComponent(DebateChatActivity::class.java.name)))
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnPositive() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers {
            positive.button.scrollAndClick()
            votingApi.success()
            images {
                positive.hasPositiveColor()
                negative.hasInactiveColor()
                neutral.hasInactiveColor()
            }
        }
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNegative() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers {
            negative.button.scrollAndClick()
            votingApi.success()
            images {
                positive.hasInactiveColor()
                negative.hasNegativeColor()
                neutral.hasInactiveColor()
            }
        }
    }

    @Test
    fun shouldChangeImagesInButtonsWhenClickedOnNeutral() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers {
            neutral.button.scrollAndClick()
            votingApi.success()
            images {
                positive.hasInactiveColor()
                negative.hasInactiveColor()
                neutral.hasNeutralColor()
            }
        }
    }

    @Test
    fun shouldHaveDisabledButtonsOnVoteCall() {
        startActivityAndSuccessfullyReturnDebateDetails()
        answers {
            negative.button.click()
            buttons {
                positive.isDisabled()
                negative.isDisabled()
                neutral.isDisabled()
            }
        }
    }

    @Test
    fun shouldHaveEnabledButtonsOnVoteCallEnd() {
        startActivityAndSuccessfullyReturnDebateDetails()
        votingApi.success()
        answers {
            negative.button.click()
            buttons {
                positive.isEnabled()
                negative.isEnabled()
                neutral.isEnabled()
            }
        }
    }

    private fun startActivity(token: String = "token") {
        rule.startActivity(DebateDetailsActivity.intent(InstrumentationRegistry.getTargetContext(), LoginCredentials(token, 111)))
    }

    private fun startActivityAndSuccessfullyReturnDebateDetails(token: String = "token", debateData: DebateData = createDebateData()) {
        startActivity(token)
        debateApi.success(debateData)
    }
}