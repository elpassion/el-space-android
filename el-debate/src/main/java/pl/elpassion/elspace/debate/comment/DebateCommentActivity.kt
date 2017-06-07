package pl.elpassion.elspace.debate.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
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

    companion object {
        private val debateAuthTokenKey = "debateAuthTokenKey"

        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateCommentActivity::class.java).apply {
                    putExtra(debateAuthTokenKey, debateToken)
                }
    }

    private val token by lazy { intent.getStringExtra(DebateCommentActivity.debateAuthTokenKey) }

    private val controller by lazy {
        DebateCommentController(this, DebateComment.ApiProvider.get(), SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_comment_activity)
        debateCommentInputText.setOnEditorActionListener { inputText, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                controller.sendComment(token, inputText.text.toString())
            }
            false
        }
        debateCommentSendButton.setOnClickListener { controller.sendComment(token, debateCommentInputText.text.toString()) }
        debateCommentCancelButton.setOnClickListener { controller.onCancel() }
    }

    override fun showLoader() = showLoader(debateCommentCoordinator)

    override fun hideLoader() = hideLoader(debateCommentCoordinator)

    override fun showSendCommentSuccess() {
        showSnackbar(getString(R.string.debate_comment_send_success), Snackbar.LENGTH_SHORT)
    }

    override fun showSendCommentError(exception: Throwable) {
        showSnackbar(getString(R.string.debate_comment_send_error))
    }

    override fun showInvalidInputError() {
        showSnackbar(getString(R.string.debate_comment_invalid_input_error))
    }

    private fun showSnackbar(text: String, length: Int = Snackbar.LENGTH_INDEFINITE) {
        Snackbar.make(debateCommentCoordinator, text, length).show()
    }

    override fun clearInput() {
        debateCommentInputText.text.clear()
    }

    override fun closeScreen() {
        finish()
    }

    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }
}