package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.subjects.PublishSubject

class DebateDetailsControllerTest {

    private val api = mock<DebateDetails.Api>()
    private val view = mock<DebateDetails.View>()
    private val controller = DebateDetailsController(api, view)
    private val debateDetailsSubject = PublishSubject.create<DebateData>()

    @Before
    fun setUp() {
        whenever(api.getDebateDetails(any())).thenReturn(debateDetailsSubject)
    }

    @Test
    fun shouldCallApiWithGivenTokenOnCreate() {
        controller.onCreate(token = "token")
        verify(api).getDebateDetails(token = "token")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenOnCreate() {
        controller.onCreate(token = "otherToken")
        verify(api).getDebateDetails(token = "otherToken")
    }

    @Test
    fun shouldShowDebateDetailsReturnedFromApiCallOnView() {
        controller.onCreate("token")
        val debateDetails = createDebateData()
        debateDetailsSubject.onNext(debateDetails)
        debateDetailsSubject.onCompleted()
        verify(view).showDebateDetails(debateDetails)
    }

    @Test
    fun shouldShowLoaderOnApiCall() {
        controller.onCreate("token")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinish() {
        controller.onCreate("token")
        debateDetailsSubject.onNext(createDebateData())
        debateDetailsSubject.onCompleted()
        verify(view).hideLoader()
    }

    private fun createDebateData(debateTopic: String = "topic", answers: Answers = createAnswers())
            = DebateData(debateTopic, answers)

    private fun createAnswers(positiveAnswer: Answer = createAnswer(1, "answerPositive"),
                              negativeAnswer: Answer = createAnswer(2, "answerNegative"),
                              neutralAnswer: Answer = createAnswer(3, "answerNeutral"))

            = Answers(positiveAnswer, negativeAnswer, neutralAnswer)

    private fun createAnswer(answerId: Long = 1, answerLabel: String = "answer")
            = Answer(answerId, answerLabel)

}

data class DebateData(val topic: String, val answers: Answers)
data class Answers(val positive: Answer, val negative: Answer, val neutral: Answer)
data class Answer(val id: Long, val value: String)


interface DebateDetails {
    interface Api {
        fun getDebateDetails(token: String): Observable<DebateData>
    }

    interface View {
        fun showDebateDetails(debateDetails: Any)
        fun showLoader()
        fun hideLoader()
    }
}

class DebateDetailsController(private val api: DebateDetails.Api, private val view: DebateDetails.View) {
    fun onCreate(token: String) {
        view.showLoader()
        api.getDebateDetails(token).subscribe(view::showDebateDetails)
        view.hideLoader()
    }
}
