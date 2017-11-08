package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.chat.createComment
import pl.elpassion.elspace.dabate.chat.createInitialsComments
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.LoginCredentials

class DebateChatControllerTest {

    private val view = mock<DebateChat.View>()
    private val service = mock<DebateChat.Service>()
    private val events = mock<DebateChat.Events>()
    private val debateRepo = mock<DebatesRepository>()
    private val initialsCommentsSubject = SingleSubject.create<InitialsComments>()
    private val onNextCommentsEvent = PublishSubject.create<Unit>()
    private val liveCommentsSubject = BehaviorSubject.create<Comment>()
    private val sendCommentSubject = SingleSubject.create<Comment>()
    private val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 100)

    @Before
    fun setUp() {
        whenever(service.initialsCommentsObservable(any(), anyOrNull())).thenReturn(initialsCommentsSubject)
        whenever(service.liveCommentsObservable(any(), any())).thenReturn(liveCommentsSubject.share())
        whenever(service.sendComment(any())).thenReturn(sendCommentSubject)
        whenever(events.onNextComments()).thenReturn(onNextCommentsEvent)
        whenever(debateRepo.getLatestDebateCode()).thenReturn("12345")
        whenever(debateRepo.areTokenCredentialsMissing(any())).thenReturn(false)
        whenever(debateRepo.getTokenCredentials(any())).thenReturn(createTokenCredentials("firstName", "lastName"))
    }

    @Test
    fun shouldCallServiceInitialsCommentsOnCreate() {
        onCreate()
        verify(service).initialsCommentsObservable(any(), anyOrNull())
    }

    @Test
    fun shouldCallServiceInitialsCommentsObservableWithGivenDataOnCreate() {
        onCreate(token = "someOtherToken")
        verify(service).initialsCommentsObservable("someOtherToken", null)
    }

    @Test
    fun shouldShowCommentsReturnedFromServiceInitialsComments() {
        val comments = listOf(createComment(), createComment())
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = comments))
        verify(view).showInitialsComments(comments)
    }

    @Test
    fun shouldShowLoaderWhenServiceInitialsCommentsStarts() {
        onCreate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenServiceInitialsCommentsEnds() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnServiceInitialsCommentsError() {
        onCreate()
        initialsCommentsSubject.onError(RuntimeException())
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenServiceInitialsComments() {
        val subscribeOn = TestScheduler()
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.onCreate(createLoginCredentials())
        initialsCommentsSubject.onError(RuntimeException())
        verify(view, never()).showInitialsCommentsError(any())
        subscribeOn.triggerActions()
        verify(view).showInitialsCommentsError(any())
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenServiceInitialsComments() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.onCreate(createLoginCredentials())
        initialsCommentsSubject.onError(RuntimeException())
        verify(view, never()).showInitialsCommentsError(any())
        observeOn.triggerActions()
        verify(view).showInitialsCommentsError(any())
    }

    @Test
    fun shouldShowInitialsCommentsErrorWhenServiceInitialsCommentsFails() {
        onCreate()
        val exception = RuntimeException()
        initialsCommentsSubject.onError(exception)
        verify(view).showInitialsCommentsError(exception)
    }

    @Test
    fun shouldShowDebateClosedErrorWhenServiceInitialsCommentsReturnsDebateClosedFlag() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = true))
        verify(view).showDebateClosedError()
    }

    @Test
    fun shouldCallServiceInitialsCommentsOnServiceInitialsCommentsRefresh() {
        controller.onInitialsCommentsRefresh(createLoginCredentials())
        verify(service).initialsCommentsObservable(any(), anyOrNull())
    }

    @Test
    fun shouldCallServiceInitialsCommentsWithGivenDataWhenNextPositionIsNullOnServiceInitialsCommentsRefresh() {
        controller.onInitialsCommentsRefresh(createLoginCredentials(token = "refreshToken"))
        verify(service).initialsCommentsObservable("refreshToken", null)
    }

    @Test
    fun shouldCallServiceInitialsCommentsWithReallyGivenDataOnServiceInitialsCommentsRefresh() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(nextPosition = 333))
        controller.onInitialsCommentsRefresh(createLoginCredentials(token = "someRefreshToken"))
        verify(service).initialsCommentsObservable("someRefreshToken", 333)
    }

    @Test
    fun shouldNotCallServiceLiveCommentsWhenServiceInitialsCommentsReturnsDebateClosedFlag() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = true))
        verify(service, never()).liveCommentsObservable(any(), any())
    }

    @Test
    fun shouldNotCallServiceLiveCommentsSecondTimeOnServiceInitialsCommentsSuccessWhenSubscribedToLiveComments() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.subscribe()
        verify(service, atMost(1)).liveCommentsObservable(any(), any())
    }

    @Test
    fun shouldCallServiceLiveCommentsWithReallyGivenDataOnCreate() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("67890")
        onCreate(userId = 456)
        initialsCommentsSubject.onSuccess(createInitialsComments())
        verify(service).liveCommentsObservable("67890", 456)
    }

    @Test
    fun shouldCallServiceLiveCommentsWhenServiceInitialsCommentsReturnsDebateNotClosedFlag() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = false))
        verify(service).liveCommentsObservable(any(), any())
    }

    @Test
    fun shouldCallServiceLiveCommentsWhenLiveCommentsSubscriptionIsNull() {
        whenever(service.liveCommentsObservable(any(), any())).thenReturn(null)
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = false))
        verify(service).liveCommentsObservable(any(), any())
    }

    @Test
    fun shouldCallServiceLiveCommentsWhenLiveCommentsSubscriptionIsDisposed() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = false))
        onNextCommentsEvent.onNext(Unit)
        liveCommentsSubject.onNext(createComment())
        verify(view, times(1)).showLiveComment(any())
    }

    @Test
    fun shouldCallServiceLiveCommentsSecondTimeOnServiceLiveCommentsRefresh() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        controller.onLiveCommentsRefresh(123)
        verify(service, times(2)).liveCommentsObservable(any(), any())
    }

    @Test
    fun shouldCallServiceLiveCommentsWithReallyGivenDataOnServiceLiveCommentsRefresh() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("34567")
        onCreate(userId = 123)
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        controller.onLiveCommentsRefresh(123)
        verify(service, times(2)).liveCommentsObservable("34567", 123)
    }

    @Test
    fun shouldCallServiceInitialsCommentsWithNonNullNextPositionOnNextComments() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        onNextCommentsEvent.onNext(Unit)
        verify(service).initialsCommentsObservable(any(), notNull())
    }

    @Test
    fun shouldCallServiceInitialsCommentsOnNextCommentsWithReallyGivenData() {
        onCreate("someToken")
        initialsCommentsSubject.onSuccess(createInitialsComments(nextPosition = 222))
        onNextCommentsEvent.onNext(Unit)
        verify(service).initialsCommentsObservable("someToken", 222)
    }

    @Test
    fun shouldNotShowMainLoaderOnNextComments() {
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments(nextPosition = 222))
        clearInvocations(view)
        onNextCommentsEvent.onNext(Unit)
        verify(view, never()).showLoader()
    }

    @Test
    fun shouldShowCommentsReturnedFromServiceLiveComments() {
        val comment = createComment(name = "LiveComment")
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onNext(comment)
        verify(view).showLiveComment(comment)
    }

    @Test
    fun shouldUseGivenSchedulerToSubscribeOnWhenServiceLiveComments() {
        val subscribeOn = TestScheduler()
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.onCreate(createLoginCredentials())
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        verify(view, never()).showLiveCommentsError(any())
        subscribeOn.triggerActions()
        verify(view).showLiveCommentsError(any())
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenServiceLiveComments() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.onCreate(createLoginCredentials())
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        verify(view, never()).showLiveCommentsError(any())
        observeOn.triggerActions()
        verify(view).showLiveCommentsError(any())
    }

    @Test
    fun shouldShowLiveCommentsErrorOnServiceLiveCommentsError() {
        val exception = RuntimeException()
        onCreate()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(exception)
        verify(view).showLiveCommentsError(exception)
    }

    @Test
    fun shouldCallServiceSendCommentWithGivenDataOnSendComment() {
        sendComment("token", "message")
        verify(service).sendComment(CommentToSend("token", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldReallyCallServiceSendCommentWithGivenDataWhenMessageIsValidOnSendComment() {
        val token = "someOtherToken"
        whenever(debateRepo.getTokenCredentials(token)).thenReturn(createTokenCredentials("NewFirstName", "NewLastName"))
        sendComment(token = "someOtherToken", message = "someOtherMessage")
        verify(service).sendComment(CommentToSend("someOtherToken", "someOtherMessage", "NewFirstName", "NewLastName"))
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsEmptyOnSendComment() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldNotSendCommentIfCommentIsEmpty() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldNotCallServiceSendCommentWhenMessageIsBlankOnSendComment() {
        sendComment(message = " ")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldNotSendCommentIfCommentIsBlank() {
        sendComment(message = "")
        verify(service, never()).sendComment(any())
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
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), Schedulers.trampoline()), maxMessageLength = 30)
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
        sendCommentSubject.onSuccess(createComment())
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
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(subscribeOn, Schedulers.trampoline()), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        sendCommentSubject.onSuccess(createComment())
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldUseGivenSchedulerToObserveOnWhenSendComment() {
        val observeOn = TestScheduler()
        val controller = DebateChatController(view, events, debateRepo, service, SchedulersSupplier(Schedulers.trampoline(), observeOn), maxMessageLength = 100)
        controller.sendComment(token = "token", message = "message")
        sendCommentSubject.onSuccess(createComment())
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldShowSendCommentSuccessPendingWhenSendCommentStatusIsPending() {
        val comment = createComment(name = "PendingComment", status = "pending")
        sendComment()
        sendCommentSubject.onSuccess(comment)
        verify(view).showSendCommentSuccessPending(comment)
    }

    @Test
    fun shouldClearSendCommentInputWhenSendCommentStatusIsNotPending() {
        sendComment()
        sendCommentSubject.onSuccess(createComment(status = "accepted"))
        verify(view).clearSendCommentInput()
    }

    @Test
    fun shouldShowErrorWhenSendCommentFailed() {
        sendComment()
        val exception = RuntimeException()
        sendCommentSubject.onError(exception)
        verify(view).showSendCommentError(exception)
    }

    @Test
    fun shouldShowDebateClosedErrorWhenServiceSendCommentReturned403CodeError() {
        sendComment()
        sendCommentSubject.onError(createHttpException(403))
        verify(view).showDebateClosedError()
    }

    @Test
    fun shouldShowCredentialDialogOnSendCommentIfCredentialsAreMissing() {
        whenever(debateRepo.areTokenCredentialsMissing("token")).thenReturn(true)
        controller.sendComment("token", "message")
        verify(view).showCredentialsDialog()
    }

    @Test
    fun shouldSaveCredentialsOnNewCredentials() {
        val token = "token"
        val credentials = createTokenCredentials()
        controller.onNewCredentials(token, credentials)
        verify(debateRepo).saveTokenCredentials(token, credentials)
    }

    @Test
    fun shouldShowFirstNameErrorOnBlankFirstName() {
        val credentials = createTokenCredentials(firstName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankFirstName() {
        val credentials = createTokenCredentials(firstName = " ")
        controller.onNewCredentials("token", credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowLastNameErrorOnBlankLastName() {
        val credentials = createTokenCredentials(lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showLastNameError()
    }

    @Test
    fun shouldNotSaveTokenCredentialsOnBlankLastName() {
        val credentials = createTokenCredentials(lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(debateRepo, never()).saveTokenCredentials(any(), any())
    }

    @Test
    fun shouldShowBothErrorsOnBlankCredentials() {
        val credentials = createTokenCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view).showLastNameError()
        verify(view).showFirstNameError()
    }

    @Test
    fun shouldCloseCredentialsDialogOnCorrectCredentials() {
        val credentials = createTokenCredentials()
        controller.onNewCredentials("token", credentials)
        verify(view).closeCredentialsDialog()
    }

    @Test
    fun shouldNotCloseCredentialDialogOnIncorrectCredentials() {
        val credentials = createTokenCredentials(firstName = " ", lastName = " ")
        controller.onNewCredentials("token", credentials)
        verify(view, never()).closeCredentialsDialog()
    }

    private fun onCreate(token: String = "token", userId: Long = 123) = controller.onCreate(createLoginCredentials(token, userId))

    private fun createTokenCredentials(firstName: String = "name", lastName: String = "lastName"): TokenCredentials = TokenCredentials(firstName, lastName)

    private fun createLoginCredentials(token: String = "token", userId: Long = 123) = LoginCredentials(token, userId)

    private fun sendComment(token: String = "token", message: String = "message") = controller.sendComment(token, message)
}