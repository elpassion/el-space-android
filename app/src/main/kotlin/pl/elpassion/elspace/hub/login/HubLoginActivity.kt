package pl.elpassion.elspace.hub.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.hub_login_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.login.shortcut.ShortcutServiceImpl
import pl.elpassion.elspace.hub.report.list.ReportListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HubLoginActivity : AppCompatActivity(), HubLogin.View {

    private val controller = HubLoginController(
            view = this,
            loginRepository = HubLoginRepositoryProvider.get(),
            shortcutService = ShortcutServiceImpl(this),
            api = HubLoginTokenApiProvider.get(),
            schedulersSupplier = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    private val googleSingInController = GoogleSingInControllerProvider.get()

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, HubLoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_login_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        hubLoginByTokenButton.setOnClickListener { controller.onLogin(tokenInput.text.toString()) }
        hubLoginHubLinkButton.setOnClickListener { controller.onHub() }
        controller.onCreate()
        googleSingInController.initializeGoogleSignIn(
                activity = this,
                onSuccess = { controller.onGoogleToken(it) },
                onFailure = {})
        hubLoginGoogleSignInButton.setOnClickListener { googleSingInController.onGoogleSignInClick() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun showEmptyLoginError() =
            Snackbar.make(hubLoginCoordinator, R.string.token_empty_error, Snackbar.LENGTH_INDEFINITE).show()

    override fun openReportListScreen() {
        ReportListActivity.start(this)
        finish()
    }

    override fun openHubWebsite() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.hub_login_uri_hub)))
        startActivity(browserIntent)
    }

    override fun showGoogleTokenError() =
            Snackbar.make(hubLoginCoordinator, R.string.google_token_error, Snackbar.LENGTH_INDEFINITE).show()

    override fun showLoader() = showLoader(hubLoginCoordinator)

    override fun hideLoader() = hideLoader(hubLoginCoordinator)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        googleSingInController.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}