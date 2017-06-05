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

    private fun sendComment() = controller.sendComment(token = "token", message = "message")

    @Before
    fun setUp() {
        whenever(api.comment(any(), any())).thenReturn(commentSubject)
    }

    @Test
    fun shouldCallApiWithGivenTokenAndMessageOnSendComment() {
        sendComment()
        verify(api).comment("token", "message")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenAndMessageOnSendComment() {
        controller.sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(api).comment("someOtherToken", "someOtherMessage")
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        sendComment()
        verify(view).showLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSendCommentIsStillInProgress() {
        sendComment()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentSucceeded() {
        sendComment()
        commentSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentFailed() {
        sendComment()
        commentSubject.onError(RuntimeException())
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSendCommentIsStillInProgress() {
        controller.run {
            sendComment()
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
        sendComment()
        commentSubject.onComplete()
        verify(view).showSendCommentSuccess()
    }

    @Test
    fun shouldShowErrorWhenSendCommentFailed() {
        sendComment()
        val exception = RuntimeException()
        commentSubject.onError(exception)
        verify(view).showSendCommentError(exception)
    }

    @Test
    fun shouldClearInputWhenSendCommentSucceeded() {
        sendComment()
        commentSubject.onComplete()
        verify(view).clearInput()
    }

    @Test
    fun shouldNotClearInputWhenSendCommentFailed() {
        sendComment()
        commentSubject.onError(RuntimeException())
        verify(view, never()).clearInput()
    }

    @Test
    fun shouldCloseScreenOnCancel() {
        controller.onCancel()
        verify(view).closeScreen()
    }
}