package pl.elpassion.elspace.debate.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithConstructors
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.support.v7.widget.scrollEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_chat_activity.*
import kotlinx.android.synthetic.main.debate_toolbar.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.LoginCredentials
import pl.elpassion.elspace.debate.chat.holders.CommentHolder
import pl.elpassion.elspace.debate.chat.holders.LoggedUserCommentHolder
import java.util.*

class DebateChatActivity : AppCompatActivity(), DebateChat.View, DebateChat.Events {

    private val credentialsDialog by lazy {
        DebateCredentialsDialog(this) { credentials ->
            controller.onNewCredentials(loginCredentials.authToken, credentials)
        }
    }

    private var scrollEventsDisposable: Disposable? = null

    private val loginCredentials by lazy { intent.getSerializableExtra(debateLoginCredentialsKey) as LoginCredentials }

    private val maxMessageLength by lazy { resources.getInteger(R.integer.debate_chat_send_comment_max_message_length) }

    private var comments = mutableListOf<Comment>()

    private val controller by lazy {
        DebateChatController(
                view = this,
                events = this,
                debateRepo = DebatesRepositoryProvider.get(),
                service = DebateChat.ServiceProvider.get(),
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()),
                maxMessageLength = maxMessageLength)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_chat_activity)
        setupUI()
        controller.onCreate(loginCredentials)
    }

    private fun setupUI() {
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        val commentsAdapter = basicAdapterWithConstructors(comments) { position ->
            createHolderForComment(comments[position])
        }.apply { setHasStableIds(true) }
        debateChatCommentsContainer.adapter = commentsAdapter
        debateChatNewMessageInfo.setOnClickListener {
            debateChatCommentsContainer.scrollToPosition(comments.indexOfLast { !it.wasShown })
        }
        debateChatSendCommentInputText.setOnEditorActionListener { inputText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                controller.sendComment(loginCredentials.authToken, inputText.text.toString())
            }
            false
        }
        debateChatSendCommentButton.setOnClickListener {
            controller.sendComment(loginCredentials.authToken, debateChatSendCommentInputText.text.toString())
        }
        debateChatSendCommentInputText.requestFocus()
        scrollEventsDisposable = debateChatCommentsContainer.scrollEvents()
                .subscribe { updateCommentsWasShownStatus() }
    }

    private fun updateCommentsWasShownStatus() {
        debateChatCommentsContainer.getVisibleItemsPositions().forEach {
            comments[it].wasShown = true
        }
        updateChatCommentsNewMessageInfo()
    }

    private fun updateChatCommentsNewMessageInfo() {
        comments.count { !it.wasShown }.also {
            if (it > 0) {
                debateChatNewMessageInfoText.text = resources.getQuantityString(R.plurals.debate_chat_live_comments_new_message_info, it, it)
                debateChatNewMessageInfo.show()
            } else {
                debateChatNewMessageInfo.hide()
            }
        }
    }

    private fun createHolderForComment(comment: Comment) = when {
        comment.userId == loginCredentials.userId -> R.layout.logged_user_comment to { itemView: View -> LoggedUserCommentHolder(itemView, timeZone) }
        else -> R.layout.comment to { itemView: View -> CommentHolder(itemView, timeZone) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun showLoader() {
        showLoader(debateChatCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateChatCoordinator)
        debateChatCommentsSwipeToRefresh.isRefreshing = false
    }

    override fun onNextComments() = debateChatCommentsSwipeToRefresh.refreshes()

    override fun showInitialsComments(initialsComments: List<Comment>) {
        debateChatCommentsContainer.run {
            if (comments.isEmpty()) {
                comments.addAll(initialsComments)
                adapter.notifyDataSetChanged()
                scrollToPosition(comments.size - 1)
            } else {
                comments.addAll(0, initialsComments)
                adapter.notifyDataSetChanged()
                scrollToPosition(initialsComments.size - 1)
            }
        }
    }

    override fun showLiveComment(liveComment: Comment) {
        val positionBeforeUpdate = comments.indexOfFirst { it.id == liveComment.id }
        val positionAfterUpdate = comments.update(liveComment)
        debateChatCommentsContainer.run {
            positionAfterUpdate.also {
                if (positionBeforeUpdate != it) {
                    adapter.notifyItemInserted(it)
                } else {
                    adapter.notifyItemChanged(it)
                }
            }
        }
        if (liveComment.userId == loginCredentials.userId && positionBeforeUpdate != positionAfterUpdate) {
            debateChatCommentsContainer.post { debateChatCommentsContainer.scrollToPosition(positionAfterUpdate) }
        } else {
            debateChatCommentsContainer.post { updateCommentsWasShownStatus() }
        }
    }

    override fun showInitialsCommentsError(exception: Throwable) {
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_initials_comments_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.debate_chat_initials_comments_error_refresh, { controller.onInitialsCommentsRefresh(loginCredentials) })
                .show()
        logExceptionIfDebug(this.localClassName, exception)
    }

    override fun showDebateClosedError() {
        debateChatSendCommentView.hide()
        debateChatSendCommentViewError.show()
    }

    override fun showLiveCommentsError(exception: Throwable) {
        Snackbar.make(debateChatCoordinator, R.string.debate_chat_live_comments_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.debate_chat_live_comments_error_refresh, { controller.onLiveCommentsRefresh(loginCredentials.userId) })
                .show()
        logExceptionIfDebug(this.localClassName, exception)
    }

    override fun showSendCommentSuccessPending(comment: Comment) {
        debateChatSendCommentInputText.text.clear()
        val position = comments.update(comment)
        debateChatCommentsContainer.run {
            adapter.notifyDataSetChanged()
            scrollToPosition(position)
        }
    }

    override fun clearSendCommentInput() {
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
        scrollEventsDisposable?.dispose()
        controller.onDestroy()
        super.onDestroy()
    }

    companion object {
        var timeZone: () -> TimeZone = { TimeZone.getDefault() }

        private val debateLoginCredentialsKey = "debateLoginCredentialsKey"

        fun start(context: Context, loginCredentials: LoginCredentials) = context.startActivity(intent(context, loginCredentials))

        fun intent(context: Context, loginCredentials: LoginCredentials) =
                Intent(context, DebateChatActivity::class.java).apply {
                    putExtra(debateLoginCredentialsKey, loginCredentials)
                }
    }
}