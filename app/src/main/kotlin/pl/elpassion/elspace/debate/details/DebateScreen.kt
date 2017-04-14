package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity

class DebateScreen : AppCompatActivity() {

    companion object {
        private val debateAuthTokenCode = "debateAuthTokenKey"

        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateScreen::class.java).apply {
                    putExtra(debateAuthTokenCode, debateToken)
                }

    }
}