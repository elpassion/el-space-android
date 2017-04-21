package pl.elpassion.elspace.debate.details

import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createDebateData
import rx.subjects.PublishSubject

class DebateDetailsActivityTest {

    private val debateDetailsSubject = PublishSubject.create<DebateData>()
    private val sendVoteSubject = PublishSubject.create<Unit>()

    @JvmField @Rule
    val rule = rule<DebateDetailsActivity> {
        DebateDetails.ApiProvider.override = {
            mock<DebateDetails.Api>().apply {
                whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject)
                whenever(vote(any(), any())).thenReturn(sendVoteSubject)
            }
        }
    }

    @Test
    fun shouldShowTopicView() {
        onId(R.id.debateTopic).isDisplayed()
    }

    @Test
    fun shouldShowPositiveAnswer() {
        onId(R.id.debatePositiveAnswer).isDisplayed()
    }

    @Test
    fun shouldShowNegativeAnswer() {
        onId(R.id.debateNegativeAnswer).isDisplayed()
    }

    @Test
    fun shouldShowNeutralAnswer() {
        onId(R.id.debateNeutralAnswer).isDisplayed()
    }

    @Test
    fun shouldShowTopicReturnedFromApi() {
        getDebateDetailsSuccessfully()
        onId(R.id.debateTopic).hasText("topic")
    }

    @Test
    fun shouldShowPositiveAnswerReturnedFromApi() {
        getDebateDetailsSuccessfully()
        onId(R.id.debatePositiveAnswer).hasText("answerPositive")
    }

    @Test
    fun shouldShowNegativeAnswerReturnedFromApi() {
        getDebateDetailsSuccessfully()
        onId(R.id.debateNegativeAnswer).hasText("answerNegative")
    }

    @Test
    fun shouldShowNeutralAnswerReturnedFromApi() {
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswer).hasText("answerNeutral")
    }

    @Test
    fun shouldShowLoaderWhenCallingApi() {
        debateDetailsSubject.onNext(createDebateData())
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldNotShowLoaderWhenApiCallFinished() {
        debateDetailsSubject.onCompleted()
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFailed() {
        debateDetailsSubject.onError(RuntimeException())
        onText(R.string.debate_details_error).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnPositiveAnswerAndApiCallFinishedSuccessfully() {
        getDebateDetailsSuccessfully()
        onId(R.id.debatePositiveAnswer).click()
        sendVoteSubject.onNext(Unit)
        sendVoteSubject.onCompleted()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnNegativeAnswerAndApiCallFinishedSuccessfully() {
        getDebateDetailsSuccessfully()
        onId(R.id.debateNegativeAnswer).click()
        sendVoteSubject.onNext(Unit)
        sendVoteSubject.onCompleted()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    @Test
    fun shouldShowVoteSuccessWhenClickOnNeutralAnswerAndApiCallFinishedSuccessfully() {
        getDebateDetailsSuccessfully()
        onId(R.id.debateNeutralAnswer).click()
        sendVoteSubject.onNext(Unit)
        sendVoteSubject.onCompleted()
        onText(R.string.debate_details_vote_success).isDisplayed()
    }

    private fun getDebateDetailsSuccessfully() {
        debateDetailsSubject.onNext(createDebateData())
        debateDetailsSubject.onCompleted()
    }
}

