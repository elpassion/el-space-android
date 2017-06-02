package pl.elpassion.elspace.debate.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.elpassion.R

class DebateCommentActivity : AppCompatActivity() {

    companion object {

        private val debateAuthTokenKey = "debateAuthTokenKey"
        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateCommentActivity::class.java).apply {
                    putExtra(debateAuthTokenKey, debateToken)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_comment_activity)
    }
}