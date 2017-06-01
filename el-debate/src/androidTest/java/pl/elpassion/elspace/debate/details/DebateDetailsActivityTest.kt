package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.scrollTo
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.junit.*
import pl.elpassion.R
import pl.elpassion.elspace.common.onToolbarBackArrow
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.debate.comment.DebateCommentActivity
import pl.elpassion.elspace.debate.details.DebateDetailsActivity.Companion.intent

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
        startActivity()
        getDebateDetailsSuccessfully()
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
    fun shouldShowHands() {
        startActivity()
        onId(R.id.debatePositiveAnswerImage).hasImage(R.drawable.hand_positive_inactive)
        onId(R.id.debateNegativeAnswerImage).hasImage(R.drawable.hand_negative_inactive)
        onId(R.id.debateNeutralAnswerImage).hasImage(R.drawable.hand_neutral_inactive)
    }

    @Test
    fun shouldShowPositiveAnswerReturnedFromApi() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debatePositiveAnswerText)
                .isDisplayed()
                .hasText("answerPositive")
    }

    @Test
    fun shouldShowNegativeAnswerReturnedFromApi() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNegativeAnswerText)
                .isDisplayed()
                .hasText("answerNegative")
    }

    @Test
    fun shouldShowNeutralAnswerReturnedFromApi() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswerText)
                .isDisplayed()
                .hasText("answerNeutral")
    }

    @Test
    fun shouldShowLoaderWhenCallingApi() {
        startActivity()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderWhenApiCallFinished() {
        startActivity()
        debateDetailsSubject.onSuccess(createDebateData())
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFailed() {
        debateDetailsSubject.onError(RuntimeException())
        startActivity()
        onText(R.string.debate_details_error).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnAnswerAndApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        voteSuccessfully()
        onId(R.id.debateNegativeAnswerButton).click()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Test
    fun shouldShowVoteErrorWhenApiCallFails() {
        startActivity()
        getDebateDetailsSuccessfully()
        sendVoteSubject.onError(RuntimeException())
        onId(R.id.debateNeutralAnswerButton).click()
        onText(R.string.debate_details_vote_error).isDisplayed()
    }

    @Test
    fun shouldNotShowVoteErrorWhenApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswerButton).click()
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
        startActivity(token)
        getDebateDetailsSuccessfully()
        onId(R.id.debatePositiveAnswerButton).click()
        verify(apiMock).vote(eq(token), any())
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingPositiveVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debatePositiveAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.positive)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNegativeVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debateNegativeAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.negative)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNeutralVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debateNeutralAnswerButton).perform(scrollTo()).click()
        verify(apiMock).vote("token", debateData.answers.neutral)
    }

    @Test
    fun shouldStartCommentActivityWhenCommentButtonClicked() {
        startActivity()
        onId(R.id.debateCommentButton).click()
        checkIntent(DebateCommentActivity::class.java)
    }

    private fun startActivity(token: String = "token") {
        val intent = intent(context = InstrumentationRegistry.getTargetContext(), debateToken = token)
        rule.launchActivity(intent)
    }

    private fun getDebateDetailsSuccessfully() {
        debateDetailsSubject.onSuccess(createDebateData())
    }

    private fun voteSuccessfully() {
        sendVoteSubject.onComplete()
    }

    private fun startActivityAndSuccessfullyReturnDebateDetails(debateData: DebateData) {
        startActivity()
        debateDetailsSubject.onSuccess(debateData)
    }
}