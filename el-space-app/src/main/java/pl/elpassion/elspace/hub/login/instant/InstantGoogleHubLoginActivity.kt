package pl.elpassion.elspace.hub.login.instant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class InstantGoogleHubLoginActivity : AppCompatActivity(), InstantGoogleHubLogin.View {

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
        InstantGoogleHubLoginController(this, provideRepository(), provideApi(), object : ShortcutService {
            override fun isSupportingShortcuts() = false
            override fun creteAppShortcuts() = Unit
        }, SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    override fun showApiLoginError() {
    }

    override fun logoutFromGoogle() {
    }

    companion object {
        private val RC_SIGN_IN = 64927
        lateinit var provideApi: () -> InstantGoogleHubLogin.Api
        lateinit var provideRepository: () -> InstantGoogleHubLogin.Repository
        lateinit var openOnLoggedInScreen: (Context) -> Unit
        lateinit var startGoogleSignInActivity: (Activity, () -> GoogleApiClient, Int) -> Unit
        lateinit var getHubGoogleSignInResult: (Intent?) -> InstantGoogleHubLogin.HubGoogleSignInResult
    }
}