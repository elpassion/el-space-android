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
import pl.elpassion.elspace.dabate.details.createPositiveAnswer
import pl.elpassion.elspace.dabate.details.createDebateData
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.dabate.details.createNegativeAnswer

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
    fun shouldShowErrorWhenApiCallFails() {
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
        val answer = createPositiveAnswer()
        createController().onVote("token", answer)
        verify(api).vote("token", answer)
    }

    @Test
    fun shouldReallyCallApiWithSelectedAnswerOnVote() {
        val answer = createNegativeAnswer()
        createController().onVote("differentToken", answer)
        verify(api).vote("differentToken", answer)
    }

    @Test
    fun shouldShowVoteLoaderAndPassCorrectAnswerOnVote() {
        val answer = createPositiveAnswer()
        createController().onVote("token", answer)
        verify(view).showVoteLoader(answer)
    }

    @Test
    fun shouldHideVoteLoaderWhenVoteApiCallIsFinished() {
        createController().onVote("token", createPositiveAnswer())
        sendVoteSubject.onComplete()
        verify(view).hideVoteLoader()
    }

    @Test
    fun shouldNotHideVoteLoaderIfVoteCallIsStillInProgress() {
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
    fun shouldShowSuccessWhenVoteSuccessfully() {
        createController().onVote("", createPositiveAnswer())
        sendVoteSubject.onComplete()
        verify(view).showVoteSuccess()
    }

    @Test
    fun shouldShowErrorWhenVoteFailed() {
        createController().onVote("", createPositiveAnswer())
        val exception = RuntimeException()
        sendVoteSubject.onError(exception)
        verify(view).showVoteError(exception)
    }

    @Test
    fun shouldResetButtonsWhenVoteFailed() {
        createController().onVote("", createPositiveAnswer())
        sendVoteSubject.onError(RuntimeException())
        verify(view).resetImagesInButtons()
    }

    @Test
    fun shouldOpenCommentScreenOnComment() {
        createController().onComment()
        verify(view).openCommentScreen()
    }

    @Test
    fun shouldShowInformationToSlowDownWithVotingOn429CodeErrorFromApi() {
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

    private fun createController(subscribeOn: Scheduler = Schedulers.trampoline(),
                                 observeOn: Scheduler = Schedulers.trampoline()) =
            DebateDetailsController(api, view, SchedulersSupplier(subscribeOn, observeOn))

    private fun returnFromApi(debateData: DebateData) {
        debateDetailsSubject.onSuccess(debateData)
    }
}