package pl.elpassion.elspace.debate.details

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.debate.details.DebateDetailsActivity.Companion.intent
import rx.Observable.never
import rx.subjects.PublishSubject

class DebateDetailsActivityTest {

    private val debateDetailsSubject = PublishSubject.create<DebateData>()
    private val sendVoteSubject = PublishSubject.create<Unit>()

    @JvmField @Rule
    val rule = rule<DebateDetailsActivity>(autoStart = false)

    @Before
    fun setup() {
        DebateDetails.ApiProvider.override = {
            mock<DebateDetails.Api>().apply {
                whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject)
                whenever(vote(any(), any())).thenReturn(sendVoteSubject)
            }
        }
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
        val apiMock = mock<DebateDetails.Api>().apply { whenever(getDebateDetails(any())).thenReturn(never()) }
        DebateDetails.ApiProvider.override = { apiMock }
        startActivity(token = "newToken")
        verify(apiMock).getDebateDetails("newToken")
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