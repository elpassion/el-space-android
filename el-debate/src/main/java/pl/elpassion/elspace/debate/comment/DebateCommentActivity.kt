package pl.elpassion.elspace.debate.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.elpassion.R

class DebateCommentActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, DebateCommentActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_comment_activity)
    }
}