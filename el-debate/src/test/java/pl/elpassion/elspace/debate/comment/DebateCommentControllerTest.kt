package pl.elpassion.elspace.debate.comment

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository
import java.lang.StringBuilder

class DebateCommentControllerTest {

    private val view = mock<DebateComment.View>()
    private val debateRepo = mock<DebatesRepository>()
    private val api = mock<DebateComment.Api>()
    private val commentSubject = CompletableSubject.create()
    private val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()))

    @Before
    fun setUp() {
        whenever(api.comment(any(), any(), any())).thenReturn(commentSubject)
        whenever(debateRepo.getLatestDebateNickname()).thenReturn("mrNick")
    }

    @Test
    fun shouldCallApiWithGivenDataOnSendComment() {
        sendComment()
        verify(api).comment("token", "message", "mrNick")
    }

    @Test
    fun shouldReallyCallApiWithGivenDataWhenMessageIsValidOnSendComment() {
        whenever(debateRepo.getLatestDebateNickname()).thenReturn("someOtherNick")
        sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(api).comment("someOtherToken", "someOtherMessage", "someOtherNick")
    }

    @Test
    fun shouldNotShowInvalidInputErrorWhenMessageIsValidOnSendComment() {
        sendComment()
        verify(view, never()).showInvalidInputError()
    }

    @Test
    fun shouldUseDefaultNicknameWhenRepoReturnsNull() {
        whenever(debateRepo.getLatestDebateNickname()).thenReturn(null)
        sendComment()
        verify(api).comment("token", "message", DEFAULT_NICKNAME)
    }

    @Test
    fun shouldNotCallApiWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(api, never()).comment(any(), any(), any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(view).showInvalidInputError()
    }

    @Test
    fun shouldNotCallApiWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(api, never()).comment(any(), any(), any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(view).showInvalidInputError()
    }

    @Test
    fun shouldNotCallApiWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = messageOverLimit)
        verify(api, never()).comment(any(), any(), any())
    }

    @Test
    fun shouldShowInputOverLimitErrorWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = messageOverLimit)
        verify(view).showInputOverLimitError()
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
        sendComment()
        controller.onDestroy()
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
        val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(subscribeOn, Schedulers.trampoline()))
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(Schedulers.trampoline(), observeOn))
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldCloseSuccessWhenSendCommentSucceeded() {
        sendComment()
        commentSubject.onComplete()
        verify(view).closeScreen()
    }

    @Test
    fun shouldShowErrorWhenSendCommentFailed() {
        sendComment()
        val exception = RuntimeException()
        commentSubject.onError(exception)
        verify(view).showSendCommentError(exception)
    }

    @Test
    fun shouldCloseScreenOnCancel() {
        controller.onCancel()
        verify(view).closeScreen()
    }

    private fun sendComment(token: String = "token", message: String = "message") = controller.sendComment(token, message)

    private val messageOverLimit = StringBuilder().apply { setLength(101) }.toString()
}