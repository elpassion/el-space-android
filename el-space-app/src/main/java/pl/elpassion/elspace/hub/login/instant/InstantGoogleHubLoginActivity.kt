package pl.elpassion.elspace.hub.login.instant

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import pl.elpassion.elspace.R

class InstantGoogleHubLoginActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openOnLoggedInScreen(this)
        startGoogleSignInActivity(this, { googleApiClientObject }, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getHubGoogleSignInResult(data)
    }

    companion object {
        private val RC_SIGN_IN = 64927
        lateinit var provideRepository: () -> InstantGoogleHubLogin.Repository
        lateinit var openOnLoggedInScreen: (Context) -> Unit
        lateinit var startGoogleSignInActivity: (Activity, () -> GoogleApiClient, Int) -> Unit
        lateinit var getHubGoogleSignInResult: (Intent?) -> InstantGoogleHubLogin.HubGoogleSignInResult
    }
}