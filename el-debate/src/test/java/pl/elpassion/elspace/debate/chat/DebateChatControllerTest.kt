package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository

class DebateChatControllerTest {

    private val service = mock<DebateChat.Service>()
    private val view = mock<DebateChat.View>()
    private val debateRepo = mock<DebatesRepository>()
    private val commentSubject = CompletableSubject.create()
    private val getCommentsList = listOf(
            GetComment(firstName = "FirstOne", lastName = "OneLast", initials = "FO", backgroundColor = 333, message = "MessOne"),
            GetComment(firstName = "FirstTwo", lastName = "TwoLast", initials = "FT", backgroundColor = 666, message = "MessTwo"))
    private val getCommentsSubject = PublishSubject.create<List<GetComment>>()
    private val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 100)

    @Before
    fun setUp() {
        whenever(service.comment(any())).thenReturn(commentSubject)
        whenever(service.getComments(any())).thenReturn(getCommentsSubject)
        whenever(debateRepo.areCredentialsMissing(any())).thenReturn(false)
        whenever(debateRepo.getTokenCredentials(any())).thenReturn(createCredentials("firstName", "lastName"))
    }

    @Test
    fun shouldCallServiceGetCommentsWithGivenTokenOnCreate() {
        onCreate()
        verify(service).getComments("token")
    }

    @Test
    fun shouldCallServiceGetCommentsWithReallyGivenTokenOnCreate() {
        val token = "someOtherToken"
        controller.onCreate(token)
        verify(service).getComments(token)
    }

    @Test
    fun shouldShowCommentsReturnedFromService() {
        onCreate()
        getCommentsSubject.onNext(getCommentsList)
        verify(view).showComments(getCommentsList)
    }
    
    @Test
    fun shouldShowLoaderOnServiceGetCommentsCall() {
        onCreate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnServiceGetCommentsNext() {
        onCreate()
        getCommentsSubject.onNext(getCommentsList)
        verify(view).hideLoader()
    }

    @Test
    fun shouldCallHideLoaderOnceOnServiceGetCommentsEmissions() {
        onCreate()
        getCommentsSubject.onNext(getCommentsList)
        getCommentsSubject.onNext(getCommentsList)
        getCommentsSubject.onNext(getCommentsList)
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldCallApiCommentWithGivenDataOnSendComment() {
        sendComment("token", "message")
        verify(service).comment(Comment("token", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldReallyCallApiCommentWithGivenDataWhenMessageIsValidOnSendComment() {
        val token = "someOtherToken"
        whenever(debateRepo.getTokenCredentials(token)).thenReturn(createCredentials("NewfirstName", "NewlastName"))
        sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(service).comment(Comment("someOtherToken", "someOtherMessage", "NewfirstName", "NewlastName"))
    }

    @Test
    fun shouldNotShowInvalidInputErrorWhenMessageIsValidOnSendComment() {
        sendComment()
        verify(view, never()).showInvalidInputError()
    }

    @Test
    fun shouldNotCallApiCommentWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(service, never()).comment(any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(view).showInvalidInputError()
    }

    @Test
    fun shouldNotCallApiCommentWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(service, never()).comment(any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(view).showInvalidInputError()
    }

    @Test
    fun shouldCallApiCommentWhenMessageIsUnderLimitOnSendComment() {
        sendComment(message = createString(100))
        verify(service).comment(any())
    }

    @Test
    fun shouldNotCallApiCommentWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = createString(101))
        verify(service, never()).comment(any())
    }

    @Test
    fun shouldUseRealMaxMessageLengthWhenMessageIsUnderLimitOnSendComment() {
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 30)
        controller.sendComment("token", createString(31))
        verify(service, never()).comment(any())
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
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        commentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
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
        whenever(debateRepo.areCredentialsMissing("token")).thenReturn(true)
        controller.sendComment("token", "message")
        verify(view).showCredentialsDialog()
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
        val credentials = createCredentials(firstName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankFirstName() {
        val credentials = createCredentials(firstName = " ")
        controller.onNewCredentials("token", credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowLastNameErrorOnBlankLastName() {
        val credentials = createCredentials(lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showLastNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankLastName() {
        val credentials = createCredentials(lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowBothErrorsOnBlankCredentials() {
        val credentials = createCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showLastNameError()
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldCloseCredentialsDialogOnCorrectCredentials() {
        val credentials = createCredentials()
        controller.onNewCredentials("token", credentials)
        verify(view).closeCredentialsDialog()
    }

    @Test
    fun shouldNotCloseCredentialDialogOnIncorrectCredentials() {
        val credentials = createCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view, never()).closeCredentialsDialog()
    }

    private fun onCreate(token: String = "token") = controller.onCreate(token)

    private fun createCredentials(firstName: String = "name", lastName: String = "lastName"): TokenCredentials = TokenCredentials(firstName, lastName)

    private fun sendComment(token: String = "token", message: String = "message") = controller.sendComment(token, message)
}