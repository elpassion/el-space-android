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
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.isDisplayedEffectively
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.chat.createComment
import pl.elpassion.elspace.dabate.chat.createInitialsComments
import pl.elpassion.elspace.dabate.chat.createSendCommentResponse
import pl.elpassion.elspace.dabate.details.createHttpException
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.LoginCredentials
import java.net.SocketException

class DebateChatActivityTest {

    private val debateRepo = mock<DebatesRepository>().apply {
        whenever(getLatestDebateCode()).thenReturn("12345")
        whenever(getTokenCredentials(any())).thenReturn(TokenCredentials("firstName", "lastName"))
        whenever(areTokenCredentialsMissing(any())).thenReturn(false)
    }
    private val sendCommentSubject = SingleSubject.create<SendCommentResponse>()
    private val initialsCommentsSubject = SingleSubject.create<InitialsComments>()
    private val liveCommentsSubject = PublishSubject.create<Comment>()
    private val service = mock<DebateChat.Service>().apply {
        whenever(liveCommentsObservable(any())).thenReturn(liveCommentsSubject)
        whenever(initialsCommentsObservable(any())).thenReturn(initialsCommentsSubject)
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
    fun shouldScrollToLastCommentOnLiveCommentsNext() {
        whenever(service.liveCommentsObservable(any())).thenReturn(Observable.just(createComment(name = "LastMessage")))
        startActivity()
        val comments = mutableListOf<Comment>().apply {
            (1..10).forEach {
                add(createComment())
            }
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
        verify(service, times(2)).initialsCommentsObservable(any())
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
    fun shouldShowLiveCommentOnServiceLiveCommentsNext() {
        whenever(service.liveCommentsObservable(any())).thenReturn(Observable.just(createComment(name = "LiveComment")))
        startActivity()
        initialsCommentsSubject.onSuccess(createInitialsComments())
        onText("LiveComment").isDisplayed()
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
        Thread.sleep(200)
        onText(R.string.debate_chat_live_comments_error_refresh).click()
        verify(service, times(2)).liveCommentsObservable(any())
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
    fun shouldHideSendCommentLayoutWhenServiceSendCommentReturned406CodeError() {
        startActivity()
        sendComment()
        sendCommentSubject.onError(createHttpException(406))
        onId(R.id.debateChatSendCommentView).isNotDisplayed()
    }

    @Test
    fun shouldShowDebateClosedErrorWhenServiceSendCommentReturned406CodeError() {
        startActivity()
        sendComment()
        sendCommentSubject.onError(createHttpException(406))
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
    fun shouldClearSendCommentInputWhenSendCommentResponseIsPending() {
        startActivity()
        sendComment("Message")
        sendCommentSubject.onSuccess(createSendCommentResponse(pending = true))
        onId(R.id.debateChatSendCommentInputText).hasText("")
    }

    @Test
    fun shouldShowSendCommentSuccessPendingInfoWhenSendCommentResponseIsPending() {
        startActivity()
        sendComment("Message")
        sendCommentSubject.onSuccess(createSendCommentResponse(pending = true))
        onText(R.string.debate_chat_send_comment_success_pending_info).isDisplayedEffectively()
    }

    @Test
    fun shouldClearSendCommentInputWhenSendCommentResponseIsNotPending() {
        startActivity()
        sendComment("Message")
        sendCommentSubject.onSuccess(createSendCommentResponse(pending = false))
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
}