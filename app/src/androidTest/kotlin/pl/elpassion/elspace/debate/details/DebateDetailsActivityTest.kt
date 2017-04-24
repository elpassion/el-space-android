package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.debate.details.DebateDetailsActivity.Companion.intent
import rx.subjects.PublishSubject

class DebateDetailsActivityTest {

    private val debateDetailsSubject = PublishSubject.create<DebateData>()
    private val sendVoteSubject = PublishSubject.create<Unit>()
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
        debateDetailsSubject.onNext(createDebateData())
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderWhenApiCallFinished() {
        startActivity()
        debateDetailsSubject.onCompleted()
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFailed() {
        startActivity()
        debateDetailsSubject.onError(RuntimeException())
        onText(R.string.debate_details_error).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnPositiveAnswerAndApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debatePositiveAnswer).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnNegativeAnswerAndApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNegativeAnswer).click()
        voteSuccessfully()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnNeutralAnswerAndApiCallFinishedSuccessfully() {
        startActivity()
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswer).click()
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
    fun shouldUseCorrectAnswerWhenSendingVote() {
        startActivity()
        val debateData = createDebateData()
        debateDetailsSubject.onNext(debateData)
        debateDetailsSubject.onCompleted()
        onId(R.id.debateNeutralAnswer).click()
        verify(apiMock).vote("token", debateData.answers.neutral)
    }

    private fun getDebateDetailsSuccessfully() {
        debateDetailsSubject.onNext(createDebateData())
        debateDetailsSubject.onCompleted()
    }

    private fun voteSuccessfully() {
        sendVoteSubject.onNext(Unit)
        sendVoteSubject.onCompleted()
    }

    private fun startActivity(token: String = "token") {
        val intent = intent(context = InstrumentationRegistry.getTargetContext(), debateToken = token)
        rule.launchActivity(intent)
    }
}