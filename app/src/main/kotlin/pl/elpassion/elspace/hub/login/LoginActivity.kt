package pl.elpassion.elspace.hub.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.hub.login.shortcut.ShortcutServiceImpl
import pl.elpassion.elspace.hub.report.list.ReportListActivity

class LoginActivity : AppCompatActivity(), Login.View {

    private val controller = LoginController(this, LoginRepositoryProvider.get(), ShortcutServiceImpl(this), object : Login.HubTokenApi {
        override fun loginWithGoogleToken() = false
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginButton.setOnClickListener { controller.onLogin(tokenInput.text.toString()) }
        loginHubButton.setOnClickListener { controller.onHub() }
        controller.onCreate()
    }

    override fun showEmptyLoginError() {
        Snackbar.make(loginCoordinator, R.string.token_empty_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun openReportListScreen() {
        ReportListActivity.start(this)
        finish()
    }

    override fun openHubWebsite() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.hub_token_uri)))
        startActivity(browserIntent)
    }

    override fun showError() = Unit
}