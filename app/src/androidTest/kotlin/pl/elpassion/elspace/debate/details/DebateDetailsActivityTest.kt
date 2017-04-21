package pl.elpassion.elspace.debate.details

import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onId
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

    @JvmField @Rule
    val rule = rule<DebateDetailsActivity> {
        DebateDetails.ApiProvider.override = { mock<DebateDetails.Api>().apply { whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject) } }
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

    private fun getDebateDetailsSuccessfully() {
        debateDetailsSubject.onNext(createDebateData())
        debateDetailsSubject.onCompleted()
    }
}

