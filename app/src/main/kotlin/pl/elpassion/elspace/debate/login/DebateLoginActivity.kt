package pl.elpassion.elspace.debate.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_login_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.DebateTokenRepositoryProvider
import pl.elpassion.elspace.debate.details.DebateDetailsActivity

class DebateLoginActivity : AppCompatActivity(), DebateLogin.View {

    private val controller by lazy {
        DebateLoginController(this, DebateTokenRepositoryProvider.get(), DebateLogin.ApiProvider.get(), SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, DebateLoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_login_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        debateLoginButton.setOnClickListener {
            controller.onLogToDebate(debateLoginInputText.text.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun openDebateScreen(authToken: String) {
        DebateDetailsActivity.start(this, authToken)
    }

    override fun showLoginFailedError() {
        Snackbar.make(debateLoginCoordinator, R.string.debate_login_fail, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showLoader() {
        showLoader(debateLoginCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateLoginCoordinator)
    }

    override fun showWrongPinError() {
        Snackbar.make(debateLoginCoordinator, R.string.debate_code_incorrect, Snackbar.LENGTH_INDEFINITE).show()
    }
}