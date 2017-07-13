package pl.elpassion.elspace.debate.comment

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository

class DebateCommentControllerTest {

    private val view = mock<DebateComment.View>()
    private val debateRepo = mock<DebatesRepository>()
    private val api = mock<DebateComment.Api>()
    private val commentSubject = CompletableSubject.create()
    private val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 100)

    @Before
    fun setUp() {
        whenever(api.comment(any(), any(), any())).thenReturn(commentSubject)
        whenever(debateRepo.areCredentialsMissing(any())).thenReturn(false)
    }

    @Test
    fun shouldCallApiWithGivenDataOnSendComment() {
        sendComment()
        verify(api).comment(eq("token"), eq("message"), any())
    }

    @Test
    fun shouldReallyCallApiWithGivenDataWhenMessageIsValidOnSendComment() {
        whenever(debateRepo.getLatestDebateNickname()).thenReturn("someOtherNick")
        sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(api).comment(eq("someOtherToken"), eq("someOtherMessage"), any())
    }

    @Test
    fun shouldNotShowInvalidInputErrorWhenMessageIsValidOnSendComment() {
        sendComment()
        verify(view, never()).showInvalidInputError()
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
    fun shouldCallApiWhenMessageIsUnderLimitOnSendComment() {
        sendComment(message = createString(100))
        verify(api).comment(any(), any(), any())
    }

    @Test
    fun shouldNotCallApiWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = createString(101))
        verify(api, never()).comment(any(), any(), any())
    }

    @Test
    fun shouldUseRealMaxMessageLengthWhenMessageIsUnderLimitOnSendComment() {
        val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 30)
        controller.sendComment("token", createString(31))
        verify(api, never()).comment(any(), any(), any())
    }

    @Test
    fun shouldShowInputOverLimitErrorWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = createString(101))
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
        val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateCommentController(view, debateRepo, api, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldCloseScreenWhenSendCommentSucceeded() {
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

    @Test
    fun shouldShowCredentialDialogOnSendCommentIfCredentialsAreMissing() {
        val token = "token"
        whenever(debateRepo.areCredentialsMissing(token)).thenReturn(true)
        controller.sendComment(token, "message")
        verify(view).showCredentialDialog()
    }

    @Test
    fun shouldSaveCredentialsOnNewCredentials() {
        val token = "token"
        val credentials = createCredentials()
        controller.onNewCredentials(token, credentials)
        verify(debateRepo).saveTokenCredentials(token, credentials)
    }

    @Test
    fun shouldShowFirstNameErrorOnBlankFirstName() {
        val token = "token"
        val credentials = createCredentials(firstName = " ")
        controller.onNewCredentials(token, credentials)
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankFirstName() {
        val token = "token"
        val credentials = createCredentials(firstName = " ")
        controller.onNewCredentials(token, credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowLastNameErrorOnBlankFirstName() {
        val token = "token"
        val credentials = createCredentials(lastName = " ")
        controller.onNewCredentials(token, credentials)
        verify(view).showLastNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankLastName() {
        val token = "token"
        val credentials = createCredentials(lastName = " ")
        controller.onNewCredentials(token, credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowBothErrorsOnBlankCredentials() {
        val token = "token"
        val credentials = createCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials(token, credentials)
        verify(view).showLastNameError()
        verify(view).showFirstNameError()
    }

    private fun createCredentials(firstName: String = "name", lastName: String = "lastName"): TokenCredentials = TokenCredentials(firstName, lastName)

    private fun sendComment(token: String = "token", message: String = "message") = controller.sendComment(token, message)
}