package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.elpassion.android.view.show
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.hub_login_activity.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.login.shortcut.ShortcutServiceImpl

class GoogleHubLoginActivity : AppCompatActivity(), GoogleHubLogin.View {

    private val googleApiClientObject: GoogleApiClient by lazy {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()
        GoogleApiClient.Builder(this)
                .enableAutoManage(this, {})
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
    }

    private val controller by lazy {
        GoogleHubLoginController(this, provideRepository(), provideApi(), ShortcutServiceImpl(this), SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hub_login_activity)
        showLoader(hubLoginCoordinator)
        controller.onCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        controller.onGoogleSignInResult(getHubGoogleSignInResult(data))
    }

    override fun openOnLoggedInScreen() {
        openOnLoggedInScreen(this)
    }

    override fun startGoogleLoginIntent() {
        startGoogleSignInActivity(this, { googleApiClientObject }, RC_SIGN_IN)
    }

    override fun showGoogleLoginError() {
        hideLoader(hubLoginCoordinator)
        googleLoginError.show()
    }

    override fun showApiLoginError() {
        hideLoader(hubLoginCoordinator)
        googleTokenError.show()
    }

    override fun logoutFromGoogle() {
        logoutFromGoogle { googleApiClientObject }
    }

    companion object {
        private val RC_SIGN_IN = 64927
        fun start(context: Context) = context.startActivity(Intent(context, GoogleHubLoginActivity::class.java))

        lateinit var provideApi: () -> GoogleHubLogin.Api
        lateinit var provideRepository: () -> GoogleHubLogin.Repository
        lateinit var openOnLoggedInScreen: (Context) -> Unit
        lateinit var startGoogleSignInActivity: (Activity, () -> GoogleApiClient, Int) -> Unit
        lateinit var getHubGoogleSignInResult: (Intent?) -> GoogleHubLogin.HubGoogleSignInResult
        lateinit var logoutFromGoogle: (() -> GoogleApiClient) -> Unit
    }
}