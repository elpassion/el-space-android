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
    private val sendCommentSubject = CompletableSubject.create()
    private val getComment = GetComment(name = "First Last", initials = "FO", backgroundColor = 333, message = "MessOne", isMine = true)
    private val getCommentsSubject = PublishSubject.create<GetComment>()
    private val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 100)

    @Before
    fun setUp() {
        whenever(service.sendComment(any())).thenReturn(sendCommentSubject)
        whenever(service.getComment(any())).thenReturn(getCommentsSubject)
        whenever(debateRepo.areCredentialsMissing(any())).thenReturn(false)
        whenever(debateRepo.getTokenCredentials(any())).thenReturn(createCredentials("firstName", "lastName"))
    }

    @Test
    fun shouldCallServiceGetCommentsWithGivenTokenOnCreate() {
        onCreate()
        verify(service).getComment("token")
    }

    @Test
    fun shouldCallServiceGetCommentsWithReallyGivenTokenOnCreate() {
        val token = "someOtherToken"
        controller.onCreate(token)
        verify(service).getComment(token)
    }

    @Test
    fun shouldShowCommentsReturnedFromService() {
        onCreate()
        getCommentsSubject.onNext(getComment)
        verify(view).showComment(getComment)
    }

    @Test
    fun shouldShowLoaderOnServiceGetCommentsCall() {
        onCreate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderOnServiceGetCommentsNext() {
        onCreate()
        getCommentsSubject.onNext(getComment)
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnServiceGetCommentsIfCallIsInProgress() {
        onCreate()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldShowCommentEveryTimeOnServiceGetCommentsEmissions() {
        onCreate()
        getCommentsSubject.onNext(getComment)
        getCommentsSubject.onNext(getComment)
        getCommentsSubject.onNext(getComment)
        getCommentsSubject.onComplete()
        verify(view, times(3)).showComment(getComment)
    }

    @Test
    fun shouldShowCommentsClosedWhenServiceGetCommentsFinished() {
        onCreate()
        getCommentsSubject.onComplete()
        verify(view).showCommentsClosed()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenServiceGetComments() {
        val subscribeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.onCreate(token = "token")
        getCommentsSubject.onNext(getComment)
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        getCommentsSubject.onNext(getComment)
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenServiceGetComments() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.onCreate(token = "token")
        getCommentsSubject.onNext(getComment)
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowGetCommentsErrorWhenServiceGetCommentsFails() {
        onCreate()
        val exception = RuntimeException()
        getCommentsSubject.onError(exception)
        verify(view).showGetCommentsError(exception)
    }

    @Test
    fun shouldCallServiceSendCommentWithGivenDataOnSendComment() {
        sendComment("token", "message")
        verify(service).sendComment(CommentToSend("token", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldReallyCallServiceSendCommentWithGivenDataWhenMessageIsValidOnSendComment() {
        val token = "someOtherToken"
        whenever(debateRepo.getTokenCredentials(token)).thenReturn(createCredentials("NewfirstName", "NewlastName"))
        sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(service).sendComment(CommentToSend("someOtherToken", "someOtherMessage", "NewfirstName", "NewlastName"))
    }

    @Test
    fun shouldNotShowInvalidInputErrorWhenMessageIsValidOnSendComment() {
        sendComment()
        verify(view, never()).showInvalidInputError()
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(view).showInvalidInputError()
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldShowInvalidInputErrorWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(view).showInvalidInputError()
    }

    @Test
    fun shouldCallServiceSendCommentWhenMessageIsUnderLimitOnSendComment() {
        sendComment(message = createString(100))
        verify(service).sendComment(any())
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsOverLimitOnSendComment() {
        sendComment(message = createString(101))
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldUseRealMaxMessageLengthWhenMessageIsUnderLimitOnSendComment() {
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 30)
        controller.sendComment("token", createString(31))
        verify(service, never()).sendComment(any())
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
        sendCommentSubject.onComplete()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentFailed() {
        sendComment()
        sendCommentSubject.onError(RuntimeException())
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
        sendCommentSubject.onComplete()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        sendCommentSubject.onComplete()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowSendCommentSuccessWhenSendCommentSucceeded() {
        sendComment()
        sendCommentSubject.onComplete()
        verify(view).showSendCommentSuccess()
    }

    @Test
    fun shouldShowErrorWhenSendCommentFailed() {
        sendComment()
        val exception = RuntimeException()
        sendCommentSubject.onError(exception)
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