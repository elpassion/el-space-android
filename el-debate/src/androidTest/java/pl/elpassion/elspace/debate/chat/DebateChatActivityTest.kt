package pl.elpassion.elspace.debate.chat

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withInputType
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
import com.elpassion.android.commons.espresso.*
import com.elpassion.android.commons.espresso.recycler.onRecyclerViewItem
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.dabate.details.createString
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider

class DebateChatActivityTest {

    private val debateRepo = mock<DebatesRepository>().apply {
        whenever(getTokenCredentials(any())).thenReturn(TokenCredentials("firstName", "lastName"))
        whenever(areCredentialsMissing(any())).thenReturn(false)
    }
    private val comment = Comment(name = "First Last", initials = "FO", backgroundColor = 333, message = "Message", isPostedByLoggedUser = true)
    private val commentSubject = PublishSubject.create<Comment>()
    private val sendCommentSubject = CompletableSubject.create()
    private val service = mock<DebateChat.Service>().apply {
        whenever(sendComment(any())).thenReturn(sendCommentSubject)
        whenever(getComment(any())).thenReturn(commentSubject)
    }

    @JvmField @Rule
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
    fun shouldShowCorrectInitialsInLoggedUserCommentView() {
        startActivity()
        commentSubject.onNext(comment)
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentView).hasChildWithText("FO")
    }

    @Test
    fun shouldShowCorrectNameInLoggedUserCommentView() {
        startActivity()
        commentSubject.onNext(comment)
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentView).hasChildWithText("First Last")
    }

    @Test
    fun shouldShowCorrectMessageInLoggedUserCommentView() {
        startActivity()
        commentSubject.onNext(comment)
        onRecyclerViewItem(R.id.debateChatCommentsContainer, 0, R.id.loggedUserCommentMessage).hasChildWithText("Message")
    }

    @Test
    fun shouldShowSendCommentHintInInputField() {
        startActivity()
        onId(R.id.debateChatSendCommentInputText).textInputEditTextHasHint(R.string.debate_comment_hint)
    }

    @Test
    fun shouldShowSendCommentButton() {
        startActivity()
        onId(R.id.debateChatSendCommentButton)
                .isDisplayed()
                .hasText(R.string.debate_comment_button_send)
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
        startActivity(debateToken = "someToken")
        sendComment()
        verify(service).sendComment(CommentToSend("someToken", "message", "firstName", "lastName"))
    }

    @Ignore
    @Test
    fun shouldUseCorrectTokenAndMessageOnSendCommentButtonClick() {
        startActivity(debateToken = "someToken")
        onId(R.id.debateChatSendCommentInputText)
                .replaceText("message")
        Espresso.closeSoftKeyboard()
        onId(R.id.debateChatSendCommentButton).click()
        verify(service).sendComment(CommentToSend("someToken", "message", "firstName", "lastName"))
    }

    @Test
    fun shouldShowInvalidInputErrorWhenInputIsEmptyOnSendComment() {
        startActivity()
        sendComment("")
        onText(R.string.debate_comment_invalid_input_error).isDisplayed()
    }

    @Test
    fun shouldShowCorrectInputOverLimitErrorMessageWhenInputIsOverLimitOnSendComment() {
        startActivity()
        val maxMessageLength = 100
        val message = InstrumentationRegistry.getTargetContext().resources.getString(R.string.debate_comment_input_over_limit_error).format(maxMessageLength)
        sendComment(createString(maxMessageLength + 1))
        onText(message).isDisplayed()
    }

    @Test
    fun shouldGetMaxMessageLengthFromResourcesWhenInputIsOverLimitOnSendComment() {
        startActivity()
        val maxMessageLength = InstrumentationRegistry.getTargetContext().resources.getInteger(R.integer.debate_comment_max_message_length)
        val message = InstrumentationRegistry.getTargetContext().resources.getString(R.string.debate_comment_input_over_limit_error).format(maxMessageLength)
        sendComment(createString(maxMessageLength + 1))
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
        onText(R.string.debate_comment_send_error).isDisplayed()
    }

    @Test
    fun shouldNotClearCommentInputWhenSendCommentFailed() {
        startActivity()
        sendComment("New message")
        sendCommentSubject.onError(RuntimeException())
        onId(R.id.debateChatSendCommentInputText).hasText("New message")
    }

    @Test
    fun shouldShowSendCommentSuccessOnSuccessfullySentComment() {
        startActivity()
        sendComment()
        sendCommentSubject.onComplete()
        onId(R.id.debateChatSendCommentInputText).hasText("")
    }

    @Test
    fun shouldShowFirstNameCredentialInputOnMissingCredentials() {
        startActivityAndOpenCredentialsDialog()
        onId(R.id.debateCredentialsFirstNameInputText)
                .isDisplayed()
                .textInputEditTextHasHint(R.string.debate_comment_credentials_first_name_hint)
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldShowLastNameCredentialInputOnMissingCredentials() {
        startActivityAndOpenCredentialsDialog()
        onId(R.id.debateCredentialsLastNameInputText)
                .isDisplayed()
                .textInputEditTextHasHint(R.string.debate_comment_credentials_last_name_hint)
                .check(matches(withInputType(TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_NORMAL)))
    }

    @Test
    fun shouldSaveProvidedCredentialsToRepo() {
        startActivityAndOpenCredentialsDialog(debateToken = "DebateToken")
        saveCredentials(firstName = "firstName", lastName = "lastName")
        verify(debateRepo).saveTokenCredentials("DebateToken", TokenCredentials("firstName", "lastName"))
    }

    @Test
    fun shouldHideCredentialDialogWhenCredentialsWereSaved() {
        startActivityAndOpenCredentialsDialog(debateToken = "DebateToken")
        saveCredentials(firstName = "firstName", lastName = "lastName")
        onId(R.id.debateCommentCredentialsDialog).doesNotExist()
    }

    @Test
    fun shouldDisplayErrorOnIncorrectFirstName() {
        startActivityAndOpenCredentialsDialog(debateToken = "DebateToken")
        saveCredentials(firstName = " ", lastName = "lastName")
        onId(R.id.debateCredentialsFirstNameInputText).editTextHasError(R.string.debate_comment_credentials_first_name_incorrect)
    }

    @Test
    fun shouldDisplayErrorOnIncorrectLastName() {
        startActivityAndOpenCredentialsDialog(debateToken = "DebateToken")
        saveCredentials(firstName = "firstName", lastName = " ")
        onId(R.id.debateCredentialsLastNameInputText).editTextHasError(R.string.debate_comment_credentials_last_name_incorrect)
    }

    @Test
    fun shouldHaveCredentialsDialogInfo() {
        startActivityAndOpenCredentialsDialog(debateToken = "DebateToken")
        onText(R.string.debate_comment_credentials_info).isDisplayed()
    }

    private fun saveCredentials(firstName: String, lastName: String) {
        onId(R.id.debateCredentialsFirstNameInputText).replaceText(firstName)
        onId(R.id.debateCredentialsLastNameInputText).replaceText(lastName)
        onText(R.string.debate_comment_credentials_confirm).click()
    }

    private fun startActivity(debateToken: String = "debateToken") {
        rule.startActivity(DebateChatActivity.intent(InstrumentationRegistry.getTargetContext(), debateToken))
    }

    private fun sendComment(message: String = "message") {
        onId(R.id.debateChatSendCommentInputText)
                .replaceText(message)
                .pressImeActionButton()
    }

    private fun startActivityAndOpenCredentialsDialog(debateToken: String = "debateToken") {
        whenever(debateRepo.areCredentialsMissing(any())).thenReturn(true)
        startActivity(debateToken = debateToken)
        sendComment()
    }
}