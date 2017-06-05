package pl.elpassion.elspace.debate.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_comment_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
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
    }

    override fun showLoader() = showLoader(debateCommentCoordinator)

    override fun hideLoader() {

    }

    override fun showSendCommentSuccess() {

    }

    override fun showSendCommentError(exception: Throwable) {

    }
}