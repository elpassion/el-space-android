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
        whenever(api.comment(any(), any())).thenReturn(commentSubject)
    }

    @Test
    fun shouldCallApiWithGivenTokenAndMessageOnSendComment() {
        controller.sendComment("token", "message")
        verify(api).comment("token", "message")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenAndMessageOnSendComment() {
        controller.sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(api).comment("someOtherToken", "someOtherMessage")
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        controller.sendComment("token", "mess")
        verify(view).showLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSendCommentIsStillInProgress() {
        controller.sendComment("token", "mess")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentSucceeded() {
        controller.sendComment("token", "mess")
        commentSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentFailed() {
        controller.sendComment("token", "mess")
        commentSubject.onError(RuntimeException())
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSendCommentIsStillInProgress() {
        controller.run {
            controller.sendComment("token", "mess")
            onDestroy()
        }
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfSendCommentWasntCalled() {
        controller.onDestroy()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenSendComment() {
        val subscribeOn = TestScheduler()
        val controller = DebateCommentController(view, api, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateCommentController(view, api, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowSuccessWhenSendCommentSucceeded() {
        controller.sendComment("token", "mess")
        commentSubject.onComplete()
        verify(view).showSendCommentSuccess()
    }

    @Test
    fun shouldShowErrorWhenSendCommentFailed() {
        controller.sendComment("token", "mess")
        val exception = RuntimeException()
        commentSubject.onError(exception)
        verify(view).showSendCommentError(exception)
    }
}