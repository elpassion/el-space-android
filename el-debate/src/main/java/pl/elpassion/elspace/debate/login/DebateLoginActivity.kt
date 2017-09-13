package pl.elpassion.elspace.debate.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.constraint_debate_login_activity.*
import kotlinx.android.synthetic.main.debate_login_activity.*
import kotlinx.android.synthetic.main.debate_toolbar.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.DebatesRepositoryProvider
import pl.elpassion.elspace.debate.LoginCredentials
import pl.elpassion.elspace.debate.details.DebateDetailsActivity

class DebateLoginActivity : AppCompatActivity(), DebateLogin.View {

    private val controller by lazy {
        DebateLoginController(this, DebatesRepositoryProvider.get(), DebateLogin.ApiProvider.get(), SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    private val debateClosedError by lazy {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.debate_login_debate_closed_error))
                .setPositiveButton(getString(R.string.debate_login_debate_closed_error_button_ok)) { dialog, _ -> dialog.dismiss() }
                .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_login_activity)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.debate_title)
        if (showBackArrow) {
            showBackArrowOnActionBar()
        }
        debateLoginButton.setOnClickListener {
            loginToDebate()
        }
        debateLoginPinInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginToDebate()
            }
            false
        }
        controller.onCreate()
    }

    private fun loginToDebate() {
        controller.onLogToDebate(debateLoginPinInputText.text.toString())
    }

    override fun fillDebateCode(debateCode: String) {
        debateLoginPinInputText.setText(debateCode)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }

    override fun openDebateScreen(loginCredentials: LoginCredentials) {
        DebateDetailsActivity.start(this, loginCredentials)
    }

    override fun showDebateClosedError() {
        debateClosedError.show()
    }

    override fun showLoginError(error: Throwable) {
        Snackbar.make(debateLoginCoordinator, R.string.debate_login_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showLoader() {
        showLoader(debateLoginCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateLoginCoordinator)
    }

    override fun showWrongPinError() {
        Snackbar.make(debateLoginCoordinator, R.string.debate_login_code_incorrect, Snackbar.LENGTH_INDEFINITE).show()
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, DebateLoginActivity::class.java))
        var showBackArrow = true
    }
}