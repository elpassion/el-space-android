package pl.elpassion.elspace.debate.chat

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
import com.elpassion.android.commons.espresso.*
import com.elpassion.android.commons.espresso.recycler.onRecyclerViewItem
import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.isDisplayedEffectively
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.chat.createComment
import pl.elpassion.elspace.dabate.chat.createInitialsComments
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.LoginCredentials
import java.net.SocketException

class DebateChatActivityTest {

    private val initialsComments = mutableListOf<Comment>().apply {
        (11..20).forEach {
            add(createComment(name = it.toString(), id = it.toLong()))
        }
    }
    private val debateRepo = mock<DebatesRepository>().apply {
        whenever(getLatestDebateCode()).thenReturn("12345")
        whenever(getTokenCredentials(any())).thenReturn(TokenCredentials("firstName", "lastName"))
        whenever(areTokenCredentialsMissing(any())).thenReturn(false)
    }
    private val initialsCommentsSubject = SingleSubject.create<InitialsComments>()
    private val liveCommentsSubject = BehaviorSubject.create<Comment>()
    private val sendCommentSubject = SingleSubject.create<Comment>()
    private val service = mock<DebateChat.Service>().apply {
        whenever(initialsCommentsObservable(any(), anyOrNull())).thenReturn(initialsCommentsSubject)
        whenever(liveCommentsObservable(any(), any())).thenReturn(liveCommentsSubject)
        whenever(sendComment(any())).thenReturn(sendCommentSubject)
    }

    @JvmField
    @Rule
    val rule = rule<DebateChatActivity>(false) {
        DebatesRepositoryProvider.override = { debateRepo }
        DebateChat.ServiceProvider.override = { service }
    }

    @Test
    fun shouldShowToolbarWithCorrectTitle() {
        startActivity()
        onId(R.id.toolbar)
                .isDisplayed()
                .hasChildWithText(R.string.debate_title)
    }

    @Test
    fun shouldExitScreenOnBackArrowClick() {
        startActivity()
        onToolbarBackArrow().click()
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldUseCorrectTokenOnServiceInitialsComments() {
        startActivity(token = "myToken")
        verify(service).initialsCommentsObservable("myToken", null)
    }

    @Test
    fun shouldShowEmptyContainerWhenServiceInitialsCommentsReturnsEmptyList() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = emptyList()))
        onId(R.id.debateChatCommentsContainer).hasChildCount(0)
    }

    @Test
    fun shouldShowCorrectInitialsInLoggedUserCommentView() {
        startActivity(userId = 1)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(userInitials = "LXX", userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentView).hasChildWithText("LXX")
    }

    @Test
    fun shouldShowCorrectNameInLoggedUserCommentView() {
        startActivity(userId = 1)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(name = "LoggedUserName", userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentView).hasChildWithText("LoggedUserName")
    }

    @Test
    fun shouldShowCorrectMessageInLoggedUserCommentView() {
        startActivity(userId = 1)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(content = "LoggedUserContent", userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentView).hasChildWithText("LoggedUserContent")
    }

    @Test
    fun shouldShowCorrectTimeInLoggedUserCommentView() {
        startActivity(userId = 1)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(createdAt = 4000000, userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentView).hasChildWithText("02:06")
    }

    @Test
    fun shouldShowCorrectInitialsInCommentView() {
        startActivity(userId = 2)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(userInitials = "NLI", userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.commentView).hasChildWithText("NLI")
    }

    @Test
    fun shouldShowCorrectNameInCommentView() {
        startActivity(userId = 2)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(name = "NotLoggedName", userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.commentView).hasChildWithText("NotLoggedName")
    }

    @Test
    fun shouldShowCorrectMessageInCommentView() {
        startActivity(userId = 2)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(content = "NotLoggedContent", userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.commentView).hasChildWithText("NotLoggedContent")
    }

    @Test
    fun shouldShowCorrectTimeInCommentView() {
        startActivity(userId = 2)
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(createdAt = 70000000, userId = 1))))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.commentView).hasChildWithText("20:26")
    }

    @Test
    fun shouldScrollToLastCommentOnInitialsCommentsSuccess() {
        startActivity()
        val comments = mutableListOf<Comment>().apply {
            (1..10).forEach {
                add(createComment())
            }
            add(createComment(content = "LastMessage"))
        }
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = comments))
        onText("LastMessage").isDisplayed()
    }

    @Test
    fun shouldShowInitialsCommentsErrorWhenServiceInitialsCommentsFails() {
        startActivity()
        initialsCommentsSubject.onError(RuntimeException())
        Thread.sleep(200)
        onText(R.string.debate_chat_initials_comments_error).isDisplayed()
    }

    @Test
    fun shouldShowInitialsCommentsRefreshButtonWithInitialsCommentsError() {
        startActivity()
        initialsCommentsSubject.onError(RuntimeException())
        onText(R.string.debate_chat_initials_comments_error_refresh).isDisplayedEffectively()
    }

    @Test
    fun shouldCallServiceInitialsCommentsSecondTimeOnRefreshInitialsCommentsClicked() {
        startActivity()
        initialsCommentsSubject.onError(RuntimeException())
        Thread.sleep(200)
        onText(R.string.debate_chat_initials_comments_error_refresh).click()
        verify(service, times(2)).initialsCommentsObservable(any(), anyOrNull())
    }

    @Test
    fun shouldUseCorrectTokenOnRefreshInitialsCommentsClicked() {
        startActivity(token = "refreshToken")
        initialsCommentsSubject.onError(RuntimeException())
        Thread.sleep(300)
        onText(R.string.debate_chat_initials_comments_error_refresh).click()
        verify(service, times(2)).initialsCommentsObservable("refreshToken", null)
    }

    @Test
    fun shouldHideSendCommentLayoutWhenServiceInitialsCommentsReturnsDebateClosedFlag() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = true))
        onId(R.id.debateChatSendCommentView).isNotDisplayed()
    }

    @Test
    fun shouldShowDebateClosedErrorWhenServiceInitialsCommentsReturnsDebateClosedFlag() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments(debateClosed = true))
        onText(R.string.debate_chat_debate_closed_error).isDisplayed()
    }

    @Test
    fun shouldCallServiceInitialsCommentsOnScrolledUpToFirstPosition() {
        startActivity(token = "scrollToken")
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = initialsComments, nextPosition = 1))
        swipeDown()
        verify(service).initialsCommentsObservable("scrollToken", 1)
    }

    @Test
    fun shouldNotCallServiceInitialsCommentsWhenNotScrolledUpAndFirstPositionIsVisible() {
        startActivity(token = "scrollToken")
        Espresso.closeSoftKeyboard()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(name = "10")), nextPosition = 1))
        verify(service, never()).initialsCommentsObservable("scrollToken", 1)
    }

    @Test
    fun shouldShowInitialsCommentsCalledFromOnNextCommentsAtBeginningOfList() {
        whenever(service.initialsCommentsObservable(any(), anyOrNull())).thenReturn(
                SingleSubject.just(createInitialsComments(comments = initialsComments, nextPosition = 1)),
                SingleSubject.just(createInitialsComments(comments = listOf(createComment(name = "1")))))
        startActivity()
        swipeDown()
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.commentView).hasChildWithText("1")
    }

    @Test
    fun shouldNotScrollToLastCommentWhenOnNextCommentsCalled() {
        whenever(service.initialsCommentsObservable(any(), anyOrNull())).thenReturn(
                SingleSubject.just(createInitialsComments(comments = initialsComments, nextPosition = 1)),
                SingleSubject.just(createInitialsComments(comments = listOf(createComment(name = "1")))))
        startActivity()
        swipeDown()
        onText("20").doesNotExist()
    }

    @Test
    fun shouldScrollToLastCommentReturnedFromOnNextComments() {
        val olderComments = mutableListOf<Comment>().apply {
            (1..10).forEach {
                add(createComment(name = it.toString(), id = it.toLong()))
            }
        }
        whenever(service.initialsCommentsObservable(any(), anyOrNull())).thenReturn(
                SingleSubject.just(createInitialsComments(comments = initialsComments, nextPosition = 1)),
                SingleSubject.just(createInitialsComments(comments = olderComments)))
        startActivity()
        swipeDown()
        Thread.sleep(200)
        onText("10").isDisplayedEffectively()
    }

    @Test
    fun shouldCallServiceLiveCommentsWithRealData() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("34567")
        startActivity(userId = 333)
        initialsCommentsSubject.onSuccess(createInitialsComments())
        Thread.sleep(200)
        verify(service).liveCommentsObservable("34567", 333)
    }

    @Test
    fun shouldShowLiveCommentOnServiceLiveCommentsNext() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onNext(createComment(name = "LiveComment"))
        onText("LiveComment").isDisplayed()
    }

    @Test
    fun shouldScrollToLastCommentOnLiveCommentsNext() {
        startActivity()
        val comments = mutableListOf<Comment>().apply {
            (1..10).forEach {
                add(createComment())
            }
        }
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = comments))
        liveCommentsSubject.onNext(createComment(name = "LastMessage", id = 100))
        onText("LastMessage").isDisplayed()
    }

    @Test
    fun shouldShowLiveCommentsErrorOnServiceLiveCommentsError() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(SocketException())
        Thread.sleep(200)
        onText(R.string.debate_chat_live_comments_error).isDisplayed()
    }

    @Test
    fun shouldShowLiveCommentsRefreshButtonWithLiveCommentsError() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        onText(R.string.debate_chat_live_comments_error_refresh).isDisplayedEffectively()
    }

    @Test
    fun shouldCallServiceLiveCommentsSecondTimeOnRefreshLiveCommentsClicked() {
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        Thread.sleep(300)
        onText(R.string.debate_chat_live_comments_error_refresh).click()
        verify(service, times(2)).liveCommentsObservable(any(), any())
    }

    @Test
    fun shouldUseRealDataOnRefreshLiveCommentsClicked() {
        whenever(debateRepo.getLatestDebateCode()).thenReturn("67890")
        startActivity(userId = 90)
        initialsCommentsSubject.onSuccess(createInitialsComments())
        liveCommentsSubject.onError(RuntimeException())
        Thread.sleep(300)
        onText(R.string.debate_chat_live_comments_error_refresh).click()
        verify(service, times(2)).liveCommentsObservable("67890", 90)
    }

    @Test
    fun shouldShowSendCommentHintInInputField() {
        startActivity()
        onId(R.id.debateChatSendCommentInputText).textInputEditTextHasHint(R.string.debate_chat_send_comment_hint)
    }

    @Test
    fun shouldShowSendCommentButton() {
        startActivity()
        onId(R.id.debateChatSendCommentButton).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectSendCommentInput() {
        startActivity()
        onId(R.id.debateChatSendCommentInputText)
                .isDisplayed()
                .replaceText("mess")
                .hasText("mess")
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldUseCorrectTokenAndMessageOnSendCommentKeyboardConfirmClick() {
        startActivity("someToken")
        sendComment()
        verify(service).sendComment(CommentToSend("someToken", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldUseCorrectTokenAndMessageOnSendCommentButtonClick() {
        startActivity("someToken")
        initialsCommentsSubject.onSuccess(createInitialsComments())
        onId(R.id.debateChatSendCommentInputText)
                .replaceText("message")
        Espresso.closeSoftKeyboard()
        onId(R.id.debateChatSendCommentButton).click()
        verify(service).sendComment(CommentToSend("someToken", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldNotSendCommentIfCommentIsEmpty() {
        startActivity()
        sendComment("")
        verify(service, never()).sendComment(any())
    }

    @Test
    fun shouldShowCorrectInputOverLimitErrorMessageWhenInputIsOverLimitOnSendComment() {
        startActivity()
        val maxMessageLength = 100
        val message = InstrumentationRegistry.getTargetContext().resources.getString(R.string.debate_chat_send_comment_input_over_limit_error).format(maxMessageLength)
        sendComment(createString(maxMessageLength + 1))
        Thread.sleep(200)
        onText(message).isDisplayed()
    }

    @Test
    fun shouldGetMaxMessageLengthFromResourcesWhenInputIsOverLimitOnSendComment() {
        startActivity()
        val maxMessageLength = InstrumentationRegistry.getTargetContext().resources.getInteger(R.integer.debate_chat_send_comment_max_message_length)
        val message = InstrumentationRegistry.getTargetContext().resources.getString(R.string.debate_chat_send_comment_input_over_limit_error).format(maxMessageLength)
        sendComment(createString(maxMessageLength + 1))
        Thread.sleep(200)
        onText(message).isDisplayed()
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        startActivity()
        sendComment()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldHideLoaderWhenSendCommentFailed() {
        startActivity()
        sendComment()
        sendCommentSubject.onError(RuntimeException())
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowSendCommentErrorWhenSendCommentFailed() {
        startActivity()
        sendComment()
        sendCommentSubject.onError(RuntimeException())
        Thread.sleep(200)
        onText(R.string.debate_chat_send_comment_error).isDisplayed()
    }

    @Test
    fun shouldHideSendCommentLayoutWhenServiceSendCommentReturned403CodeError() {
        startActivity()
        sendComment()
        sendCommentSubject.onError(createHttpException(403))
        onId(R.id.debateChatSendCommentView).isNotDisplayed()
    }

    @Test
    fun shouldShowDebateClosedErrorWhenServiceSendCommentReturned403CodeError() {
        startActivity()
        sendComment()
        sendCommentSubject.onError(createHttpException(403))
        onText(R.string.debate_chat_debate_closed_error).isDisplayed()
    }

    @Test
    fun shouldNotClearCommentInputWhenSendCommentFailed() {
        startActivity()
        sendComment("New message")
        sendCommentSubject.onError(RuntimeException())
        onId(R.id.debateChatSendCommentInputText).hasText("New message")
    }

    @Test
    fun shouldClearSendCommentInputWhenSendCommentStatusIsPending() {
        startActivity()
        sendComment()
        sendCommentSubject.onSuccess(createComment(status = "pending"))
        onId(R.id.debateChatSendCommentInputText).hasText("")
    }

    @Test
    fun shouldShowPendingCommentWhenSendCommentStatusIsPending() {
        startActivity(userId = 1)
        sendComment()
        sendCommentSubject.onSuccess(createComment(name = "PendingComment", userId = 1, status = "pending"))
        onText("PendingComment").isDisplayed()
    }

    @Test
    fun shouldShowCommentStatusViewWhenSendCommentStatusIsPending() {
        startActivity(userId = 1)
        sendComment()
        sendCommentSubject.onSuccess(createComment(userId = 1, status = "pending"))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentStatus).isDisplayed()
    }

    @Test
    fun shouldShowCommentStatusPendingTextWhenSendCommentStatusIsPending() {
        startActivity(userId = 1)
        sendComment()
        sendCommentSubject.onSuccess(createComment(userId = 1, status = "pending"))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentStatus).hasText(R.string.debate_chat_comment_status_pending)
    }

    @Test
    fun shouldHideCommentStatusViewWhenCommentStatusChangedToAccepted() {
        startActivity(userId = 1)
        sendComment()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(userId = 2))))
        sendCommentSubject.onSuccess(createComment(id = 123, userId = 1, status = "pending"))
        liveCommentsSubject.onNext(createComment(id = 123, userId = 1, status = "accepted"))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 1, R.id.loggedUserCommentStatus).isNotDisplayed()
    }

    @Test
    fun shouldNotHideCommentStatusViewWhenServiceLiveCommentsReturnedCommentWithDifferentId() {
        startActivity(userId = 1)
        sendComment()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(userId = 2))))
        sendCommentSubject.onSuccess(createComment(id = 123, userId = 1, status = "pending"))
        liveCommentsSubject.onNext(createComment(id = 124, userId = 1, status = "accepted"))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 1, R.id.loggedUserCommentStatus).isDisplayed()
    }

    @Test
    fun shouldShowCommentStatusViewWhenSendCommentStatusIsRejected() {
        startActivity(userId = 1)
        sendComment()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(userId = 2))))
        sendCommentSubject.onSuccess(createComment(id = 123, userId = 1, status = "pending"))
        liveCommentsSubject.onNext(createComment(id = 123, userId = 1, status = "rejected"))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 1, R.id.loggedUserCommentStatus).isDisplayed()
    }

    @Test
    fun shouldShowCommentStatusRejectedTextWhenSendCommentStatusChangedToRejected() {
        startActivity(userId = 1)
        sendComment()
        initialsCommentsSubject.onSuccess(createInitialsComments(comments = listOf(createComment(userId = 2))))
        sendCommentSubject.onSuccess(createComment(id = 123, userId = 1, status = "pending"))
        liveCommentsSubject.onNext(createComment(id = 123, userId = 1, status = "rejected"))
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 1, R.id.loggedUserCommentStatus).hasText(R.string.debate_chat_comment_status_rejected)
    }

    @Test
    fun shouldClearSendCommentInputWhenSendCommentStatusIsNotPending() {
        startActivity()
        sendComment()
        sendCommentSubject.onSuccess(createComment(status = "accepted"))
        onId(R.id.debateChatSendCommentInputText).hasText("")
    }

    @Test
    fun shouldShowFirstNameCredentialInputOnMissingCredentials() {
        startActivityAndOpenCredentialsDialog()
        onId(R.id.debateCredentialsFirstNameInputText)
                .isDisplayed()
                .textInputEditTextHasHint(R.string.debate_chat_credentials_first_name_hint)
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldShowLastNameCredentialInputOnMissingCredentials() {
        startActivityAndOpenCredentialsDialog()
        onId(R.id.debateCredentialsLastNameInputText)
                .isDisplayed()
                .textInputEditTextHasHint(R.string.debate_chat_credentials_last_name_hint)
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldSaveProvidedCredentialsToRepo() {
        startActivityAndOpenCredentialsDialog("DebateToken")
        saveCredentials(firstName = "firstName", lastName = "lastName")
        verify(debateRepo).saveTokenCredentials("DebateToken", TokenCredentials("firstName", "lastName"))
    }

    @Test
    fun shouldHideCredentialDialogWhenCredentialsWereSaved() {
        startActivityAndOpenCredentialsDialog("DebateToken")
        saveCredentials(firstName = "firstName", lastName = "lastName")
        onId(R.id.debateCommentCredentialsDialog).doesNotExist()
    }

    @Test
    fun shouldDisplayErrorOnIncorrectFirstName() {
        startActivityAndOpenCredentialsDialog("DebateToken")
        initialsCommentsSubject.onSuccess(createInitialsComments())
        saveCredentials(firstName = " ", lastName = "lastName")
        onId(R.id.debateCredentialsFirstNameInputText).editTextHasError(R.string.debate_chat_credentials_first_name_incorrect)
    }

    @Test
    fun shouldDisplayErrorOnIncorrectLastName() {
        startActivityAndOpenCredentialsDialog("DebateToken")
        saveCredentials(firstName = "firstName", lastName = " ")
        onId(R.id.debateCredentialsLastNameInputText).editTextHasError(R.string.debate_chat_credentials_last_name_incorrect)
    }

    @Test
    fun shouldHaveCredentialsDialogInfo() {
        startActivityAndOpenCredentialsDialog("DebateToken")
        onText(R.string.debate_chat_credentials_info).isDisplayed()
    }

    private fun saveCredentials(firstName: String, lastName: String) {
        onId(R.id.debateCredentialsFirstNameInputText).replaceText(firstName)
        onId(R.id.debateCredentialsLastNameInputText).replaceText(lastName)
        onText(R.string.debate_chat_credentials_confirm).click()
    }

    private fun startActivity(token: String = "debateToken", userId: Long = 333) {
        rule.startActivity(DebateChatActivity.intent(InstrumentationRegistry.getTargetContext(), LoginCredentials(token, userId)))
    }

    private fun sendComment(message: String = "message") {
        onId(R.id.debateChatSendCommentInputText)
                .replaceText(message)
                .pressImeActionButton()
    }

    private fun startActivityAndOpenCredentialsDialog(token: String = "debateToken") {
        whenever(debateRepo.areTokenCredentialsMissing(any())).thenReturn(true)
        startActivity(token)
        sendComment()
    }

    private fun swipeDown() {
        Espresso.closeSoftKeyboard()
        onId(R.id.debateChatCommentsContainer).swipeDown()
        onId(R.id.debateChatCommentsContainer).swipeDown()
    }
}