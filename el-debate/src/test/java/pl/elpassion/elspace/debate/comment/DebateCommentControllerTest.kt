package pl.elpassion.elspace.debate.comment

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier

class DebateCommentControllerTest {

    private val view = mock<DebateComment.View>()
    private val api = mock<DebateComment.Api>()
    private val commentSubject = CompletableSubject.create()
    private val controller = DebateCommentController(view, api, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))

    @Before
    fun setUp() {
        whenever(api.comment(any())).thenReturn(commentSubject)
    }

    @Test
    fun shouldCallApiWithMessageOnSendComment() {
        controller.sendComment("message")
        verify(api).comment("message")
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        controller.sendComment("message")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinished() {
        controller.sendComment("message")
        commentSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSendCommentCallIsStillInProgress() {
        controller.sendComment("message")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenSendComment() {
        val subscribeOn = TestScheduler()
        val controller = DebateCommentController(view, api, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.sendComment("message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateCommentController(view, api, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.sendComment("mess")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowSuccessWhenSendCommentSuccessfully() {
        controller.sendComment("mess")
        commentSubject.onComplete()
        verify(view).showSendCommentSuccess()
    }
}