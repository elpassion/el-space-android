package pl.elpassion.elspace.debate.login

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.debate_login_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.showLoader

class DebateLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_login_activity)
        loginButton.setOnClickListener { showLoader(debateLoginCoordinator) }
        Snackbar.make(debateLoginCoordinator, R.string.debate_code_incorrect, Snackbar.LENGTH_INDEFINITE).show()
    }
}