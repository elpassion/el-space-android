package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.dabate.details.createPositiveAnswer

class DebateDetailsControllerTest {

    private val api = mock<DebateDetails.Api>()
    private val view = mock<DebateDetails.View>()
    private val debateDetailsSubject = SingleSubject.create<DebateData>()
    private val sendVoteSubject = CompletableSubject.create()

    @Before
    fun setUp() {
        whenever(api.getDebateDetails(any())).thenReturn(debateDetailsSubject)
        whenever(api.vote(any(), any())).thenReturn(sendVoteSubject)
    }

    @Test
    fun shouldCallApiWithGivenTokenOnCreate() {
        createController().onCreate(token = "token")
        verify(api).getDebateDetails(token = "token")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenOnCreate() {
        createController().onCreate(token = "otherToken")
        verify(api).getDebateDetails(token = "otherToken")
    }

    @Test
    fun shouldShowDebateDetailsReturnedFromApiCallOnView() {
        createController().onCreate("token")
        val debateDetails = createDebateData()
        returnFromApi(debateDetails)
        verify(view).showDebateDetails(debateDetails)
    }

    @Test
    fun shouldShowLoaderOnApiCall() {
        createController().onCreate("token")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinishes() {
        createController().onCreate("token")
        returnFromApi(createDebateData())
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderWhenApiCallIsNotFinished() {
        createController().onCreate("token")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOn() {
        val subscribeOn = TestScheduler()
        createController(subscribeOn = subscribeOn).onCreate("token")
        returnFromApi(createDebateData())
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOn() {
        val observeOn = TestScheduler()
        createController(observeOn = observeOn).onCreate("token")
        returnFromApi(createDebateData())
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowDebateDetailsErrorWhenApiCallFails() {
        createController().onCreate("token")
        val exception = RuntimeException()
        debateDetailsSubject.onError(exception)
        verify(view).showDebateDetailsError(exception)
    }

    @Test
    fun shouldHideLoaderOnDestroyIfApiCallIsRunning() {
        createController().run {
            onCreate("token")
            onDestroy()
        }
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfApiCallWasntTriggered() {
        createController().onDestroy()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldCallApiOnDebateDetailsRefresh() {
        createController().onDebateDetailsRefresh("newToken")
        verify(api).getDebateDetails("newToken")
    }

    @Test
    fun shouldCallApiWithSelectedAnswerOnVote() {
        val negativeAnswer = Negative(2, "answerNegative")
        createController().onVote("token", negativeAnswer)
        verify(api).vote("token", negativeAnswer)
    }

    @Test
    fun shouldReallyCallApiWithSelectedAnswerOnVote() {
        val positiveAnswer = Positive(1, "answer")
        createController().onVote("differentToken", positiveAnswer)
        verify(api).vote("differentToken", positiveAnswer)
    }

    @Test
    fun shouldShowLoaderOnVote() {
        val positiveAnswer = createPositiveAnswer()
        createController().onVote("token", positiveAnswer)
        verify(view).showVoteLoader(positiveAnswer)
    }

    @Test
    fun shouldHideLoaderWhenVoteApiCallIsFinished() {
        createController().onVote("token", createPositiveAnswer())
        sendVoteSubject.onComplete()
        verify(view).hideVoteLoader()
    }

    @Test
    fun shouldNotHideLoaderIfVoteCallIsStillInProgress() {
        createController().onVote("token", createPositiveAnswer())
        verify(view, never()).hideVoteLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenVote() {
        val subscribeOn = TestScheduler()
        createController(subscribeOn = subscribeOn).onVote("token", createPositiveAnswer())
        sendVoteSubject.onComplete()
        verify(view, never()).hideVoteLoader()
        subscribeOn.triggerActions()
        verify(view).hideVoteLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenVote() {
        val observeOn = TestScheduler()
        createController(observeOn = observeOn).onVote("token", createPositiveAnswer())
        sendVoteSubject.onComplete()
        verify(view, never()).hideVoteLoader()
        observeOn.triggerActions()
        verify(view).hideVoteLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfVoteCallIsStillInProgress() {
        createController().run {
            onVote("", createPositiveAnswer())
            onDestroy()
        }
        verify(view).hideVoteLoader()
    }

    @Test
    fun shouldShowSuccessAndPassAnswerWhenVoteSuccessfully() {
        val positiveAnswer = createPositiveAnswer()
        createController().onVote("", positiveAnswer)
        sendVoteSubject.onComplete()
        verify(view).showVoteSuccess(positiveAnswer)
    }

    @Test
    fun shouldShowDebateClosedErrorOnVote406CodeErrorFromApi() {
        createController().onVote("token", createPositiveAnswer())
        sendVoteSubject.onError(createHttpException(406))
        verify(view).showDebateClosedError()
    }

    @Test
    fun shouldShowSlowDownInformationOnVote429CodeErrorFromApi() {
        createController().onVote("", createPositiveAnswer())
        val exception = createHttpException(429)
        sendVoteSubject.onError(exception)
        verify(view).showSlowDownInformation()
    }

    @Test
    fun shouldNotShowVoteErrorOn429ErrorFromApi() {
        createController().onVote("", createPositiveAnswer())
        val exception = createHttpException(429)
        sendVoteSubject.onError(exception)
        verify(view, never()).showVoteError(any())
    }

    @Test
    fun shouldShowErrorWhenVoteFailed() {
        createController().onVote("", createPositiveAnswer())
        val exception = RuntimeException()
        sendVoteSubject.onError(exception)
        verify(view).showVoteError(exception)
    }

    @Test
    fun shouldOpenChatScreenOnChat() {
        createController().onChat()
        verify(view).openChatScreen()
    }

    private fun createController(subscribeOn: Scheduler = Schedulers.trampoline(),
                                 observeOn: Scheduler = Schedulers.trampoline()) =
            DebateDetailsController(api, view, SchedulersSupplier(subscribeOn, observeOn))

    private fun returnFromApi(debateData: DebateData) {
        debateDetailsSubject.onSuccess(debateData)
    }
}