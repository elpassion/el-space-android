package pl.elpassion.elspace.hub.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.login.shortcut.ShortcutServiceImpl
import pl.elpassion.elspace.hub.report.list.ReportListActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoginActivity : AppCompatActivity(), Login.View {

    private val controller = LoginController(
            view = this,
            loginRepository = LoginRepositoryProvider.get(),
            shortcutService = ShortcutServiceImpl(this),
            api = LoginHubTokenApiProvider.get(),
            schedulersSupplier = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    private val googleSingInController = GoogleSingInControllerProvider.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginButton.setOnClickListener { controller.onLogin(tokenInput.text.toString()) }
        loginHubButton.setOnClickListener { controller.onHub() }
        controller.onCreate()
        googleSignInContainer.addView(getSignInButton())
    }

    override fun showEmptyLoginError() =
            Snackbar.make(loginCoordinator, R.string.token_empty_error, Snackbar.LENGTH_INDEFINITE).show()

    override fun openReportListScreen() {
        ReportListActivity.start(this)
        finish()
    }

    override fun openHubWebsite() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.hub_token_uri)))
        startActivity(browserIntent)
    }

    override fun showGoogleTokenError() =
            Snackbar.make(loginCoordinator, R.string.google_token_error, Snackbar.LENGTH_INDEFINITE).show()

    override fun showLoader() = showLoader(loginCoordinator)

    override fun hideLoader() = hideLoader(loginCoordinator)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        googleSingInController.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getSignInButton() = googleSingInController.initializeGoogleSingInButton(
            activity = this,
            onSuccess = { controller.onGoogleToken(it) },
            onFailure = {})
}