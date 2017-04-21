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
        onId(R.id.debateDetailsTopic).isDisplayed()
    }

    @Test
    fun shouldShowPositiveAnswer() {
        onId(R.id.debateDetailsPositiveAnswer).isDisplayed()
    }

    @Test
    fun shouldShowNegativeAnswer() {
        onId(R.id.debateDetailsNegativeAnswer).isDisplayed()
    }

    @Test
    fun shouldShowNeutralAnswer() {
        onId(R.id.debateDetailsNeutralAnswer).isDisplayed()
    }

    @Test
    fun shouldShowTopicReturnedFromApi() {
        debateDetailsSubject.onNext(createDebateData())
        debateDetailsSubject.onCompleted()
        onId(R.id.debateDetailsTopic).hasText("topic")
    }

    @Test
    fun shouldShowPositiveAnswerReturnedFromApi() {
        debateDetailsSubject.onNext(createDebateData())
        debateDetailsSubject.onCompleted()
        onId(R.id.debateDetailsPositiveAnswer).hasText("answerPositive")
    }
}

