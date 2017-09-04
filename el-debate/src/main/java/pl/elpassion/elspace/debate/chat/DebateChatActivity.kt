package pl.elpassion.elspace.debate.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithConstructors
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_chat_activity.*
import kotlinx.android.synthetic.main.debate_toolbar.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.logExceptionIfDebug
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.LoginCredentials
import pl.elpassion.elspace.debate.chat.holders.CommentHolder
import pl.elpassion.elspace.debate.chat.holders.LoggedUserCommentHolder

class DebateChatActivity : AppCompatActivity(), DebateChat.View {

    private val credentialsDialog by lazy {
        DebateCredentialsDialog(this) { credentials ->
            controller.onNewCredentials(loginCredentials.authToken, credentials)
        }
    }

    private val loginCredentials by lazy { intent.getSerializableExtra(debateLoginCredentialsKey) as LoginCredentials }

    private val maxMessageLength by lazy { resources.getInteger(R.integer.debate_chat_send_comment_max_message_length) }

    private var comments = mutableListOf<Comment>()

    private val controller by lazy {
        DebateChatController(
                view = this,
                debateRepo = DebatesRepositoryProvider.get(),
                service = DebateChat.ServiceProvider.get(),
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()),
                maxMessageLength = maxMessageLength)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_chat_activity)
        setupUI()
        controller.onCreate(loginCredentials.authToken)
    }

    private fun setupUI() {
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        debateChatCommentsContainer.layoutManager = LinearLayoutManager(this)
        debateChatCommentsContainer.adapter = basicAdapterWithConstructors(comments) { position ->
            createHolderForComment(comments[position])
        }
        debateChatSendCommentInputText.setOnEditorActionListener { inputText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                controller.sendComment(loginCredentials.authToken, inputText.text.toString())
            }
            false
        }
        debateChatSendCommentButton.setOnClickListener { controller.sendComment(loginCredentials.authToken, debateChatSendCommentInputText.text.toString()) }
        debateChatSendCommentInputText.requestFocus()
    }

    private fun createHolderForComment(comment: Comment) = when {
        comment.userId == loginCredentials.userId -> R.layout.logged_user_comment to ::LoggedUserCommentHolder
        else -> R.layout.comment to ::CommentHolder
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun showLoader() {
        showLoader(debateChatCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateChatCoordinator)
    }

    override fun showInitialsComments(initialsComments: List<Comment>) {
        comments.addAll(initialsComments)
        updateAdapter()
    }

    override fun showLiveComment(liveComment: Comment) {
        comments.add(liveComment)
        updateAdapter()
    }

    private fun updateAdapter() {
        debateChatCommentsContainer.adapter.notifyDataSetChanged()
        debateChatCommentsContainer.layoutManager.scrollToPosition(comments.size - 1)
    }

    override fun showInitialsCommentsError(exception: Throwable) {
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_initials_comments_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.debate_chat_initials_comments_error_refresh, { controller.onInitialsCommentsRefresh(loginCredentials.authToken) })
                .show()
        logExceptionIfDebug(this.localClassName, exception)
    }

    override fun showDebateClosedError() {
        debateChatSendCommentView.hide()
        debateChatSendCommentViewError.show()
    }

    override fun showLiveCommentsError(exception: Throwable) {
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_live_comments_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.debate_chat_live_comments_error_refresh, { controller.onLiveCommentsRefresh() })
                .show()
        logExceptionIfDebug(this.localClassName, exception)
    }

    override fun showSendCommentSuccessPending() {
        clearSendCommentInputText()
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_send_comment_success_pending_info, Snackbar.LENGTH_SHORT).show()
    }

    override fun clearSendCommentInput() {
        clearSendCommentInputText()
    }

    private fun clearSendCommentInputText() {
        debateChatSendCommentInputText.text.clear()
    }

    override fun showSendCommentError(exception: Throwable) {
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_send_comment_error, 3000).show()
        logExceptionIfDebug(this.localClassName, exception)
    }

    override fun showInputOverLimitError() {
        val message = getString(R.string.debate_chat_send_comment_input_over_limit_error).format(maxMessageLength)
        Snackbar.make(debateChatCoordinator, message, 3000).show()
    }

    override fun showCredentialsDialog() {
        credentialsDialog.show()
    }

    override fun closeCredentialsDialog() {
        credentialsDialog.hide()
    }

    override fun showFirstNameError() {
        credentialsDialog.showFirstNameError()
    }

    override fun showLastNameError() {
        credentialsDialog.showLastNameError()
    }

    override fun onDestroy() {
        credentialsDialog.dismiss()
        controller.onDestroy()
        super.onDestroy()
    }

    companion object {
        private val debateLoginCredentialsKey = "debateLoginCredentialsKey"

        fun start(context: Context, loginCredentials: LoginCredentials) = context.startActivity(intent(context, loginCredentials))

        fun intent(context: Context, loginCredentials: LoginCredentials) =
                Intent(context, DebateChatActivity::class.java).apply {
                    putExtra(debateLoginCredentialsKey, loginCredentials)
                }
    }

}
