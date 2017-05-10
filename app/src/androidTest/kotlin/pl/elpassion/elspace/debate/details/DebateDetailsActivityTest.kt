package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createDebateData
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
                .hasChildWithText(R.string.debate_details_title)
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
    fun shouldShowPositiveAnswerReturnedFromApi() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debatePositiveAnswer)
                .isDisplayed()
                .hasText("answerPositive")
    }

    @Test
    fun shouldShowNegativeAnswerReturnedFromApi() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNegativeAnswer)
                .isDisplayed()
                .hasText("answerNegative")
    }

    @Test
    fun shouldShowNeutralAnswerReturnedFromApi() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswer)
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
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        onText(R.string.debate_details_error).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnAnswerAndApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNegativeAnswer).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Test
    fun shouldShowVoteErrorWhenApiCallFails() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswer).click()
        sendVoteSubject.onError(RuntimeException())
        onText(R.string.debate_details_vote_error).isDisplayed()
    }

    @Test
    fun shouldNotShowVoteErrorWhenApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswer).click()
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
        onId(R.id.debatePositiveAnswer).click()
        verify(apiMock).vote(eq(token), any())
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingPositiveVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debatePositiveAnswer).click()
        verify(apiMock).vote("token", debateData.answers.positive)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNegativeVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debateNegativeAnswer).click()
        verify(apiMock).vote("token", debateData.answers.negative)
    }

    @Test
    fun shouldUseCorrectAnswerWhenSendingNeutralVote() {
        val debateData = createDebateData()
        startActivityAndSuccessfullyReturnDebateDetails(debateData)
        onId(R.id.debateNeutralAnswer).click()
        verify(apiMock).vote("token", debateData.answers.neutral)
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