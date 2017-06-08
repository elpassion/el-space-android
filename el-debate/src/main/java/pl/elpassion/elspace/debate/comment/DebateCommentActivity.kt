package pl.elpassion.elspace.debate.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_comment_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader


class DebateCommentActivity : AppCompatActivity(), DebateComment.View {

    private val token by lazy { intent.getStringExtra(debateAuthTokenKey) }

    private val controller by lazy {
        DebateCommentController(
                view = this,
                api = DebateComment.ApiProvider.get(),
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_comment_activity)
        setupUI()
    }

    private fun setupUI() {
        setTitle(R.string.debate_comment_hint)
        debateCommentInputText.setOnEditorActionListener { inputText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                controller.sendComment(token, inputText.text.toString())
            }
            false
        }
        debateCommentSendButton.setOnClickListener { debateCommentInputText.simulateImeClick() }
        debateCommentCancelButton.setOnClickListener { controller.onCancel() }
    }

    override fun showLoader() {
        showLoader(debateCommentCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateCommentCoordinator)
    }

    override fun showSendCommentError(exception: Throwable) {
        debateCommentInputLayout.error = getString(R.string.debate_comment_send_error)
    }

    override fun showInvalidInputError() {
        debateCommentInputLayout.error = getString(R.string.debate_comment_invalid_input_error)
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
                Intent(context, DebateCommentActivity::class.java).apply {
                    putExtra(debateAuthTokenKey, debateToken)
                }
    }
}

private fun TextInputEditText.simulateImeClick() {
    onEditorAction(EditorInfo.IME_ACTION_DONE)
}