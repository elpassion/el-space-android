package pl.elpassion.elspace.debate.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithConstructors
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_chat_activity.*
import kotlinx.android.synthetic.main.debate_toolbar.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.chat.holders.CommentHolder
import pl.elpassion.elspace.debate.chat.holders.LoggedUserCommentHolder

class DebateChatActivity : AppCompatActivity(), DebateChat.View {

    private val credentialsDialog by lazy {
        DebateCredentialsDialog(this) { credentials ->
            controller.onNewCredentials(token, credentials)
        }
    }

    private val token by lazy { intent.getStringExtra(debateAuthTokenKey) }

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
        controller.onCreate(token)
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
                controller.sendComment(token, inputText.text.toString())
            }
            false
        }
        debateChatSendCommentButton.setOnClickListener { controller.sendComment(token, debateChatSendCommentInputText.text.toString()) }
    }

    private fun createHolderForComment(comment: Comment) = when {
        comment.isPostedByLoggedUser -> R.layout.logged_user_comment to ::LoggedUserCommentHolder
        else -> R.layout.comment to ::CommentHolder
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun showLoader() {
        showLoader(debateChatCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateChatCoordinator)
    }

    override fun showComment(comment: Comment) {
        comments.add(comment)
        debateChatCommentsContainer.adapter.notifyDataSetChanged()
    }

    override fun showGetCommentFinished() {
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_get_comment_finished, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showGetCommentError(exception: Throwable) {

    }

    override fun showSendCommentSuccess() {
        debateChatSendCommentInputText.text.clear()
    }

    override fun showSendCommentError(exception: Throwable) {
        showErrorInInput(R.string.debate_chat_send_comment_error)
    }

    override fun showInvalidInputError() {
        showErrorInInput(R.string.debate_chat_send_comment_invalid_input_error)
    }

    override fun showInputOverLimitError() {
        val message = getString(R.string.debate_chat_send_comment_input_over_limit_error).format(maxMessageLength)
        showErrorInInput(message)
    }

    private fun showErrorInInput(@StringRes message: Int) {
        debateChatSendCommentInputLayout.error = getString(message)
    }

    private fun showErrorInInput(message: String) {
        debateChatSendCommentInputLayout.error = message
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

    override fun closeScreen() {
        finish()
    }

    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }

    companion object {
        private val debateAuthTokenKey = "debateAuthTokenKey"

        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateChatActivity::class.java).apply {
                    putExtra(debateAuthTokenKey, debateToken)
                }
    }

}
