package pl.elpassion.elspace.hub.login

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import pl.elpassion.R
import pl.elpassion.elspace.hub.login.GoogleSingInDI.getELPGoogleSignInResultFromIntent
import pl.elpassion.elspace.hub.login.GoogleSingInDI.startGoogleSignInActivity

class GoogleSingInController : GoogleApiClient.OnConnectionFailedListener {

    fun onGoogleSignInClick() {
        onGoogleClick()
    }

    private val RC_SIGN_IN = 64927
    private lateinit var onSuccess: (String) -> Unit
    private lateinit var onFailure: () -> Unit
    private lateinit var onGoogleClick: OnGoogleClick

    fun initializeGoogleSignIn(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        this.onSuccess = onSuccess
        this.onFailure = onFailure
        val googleApiClient: GoogleApiClient = getGoogleApiClient(activity)
        this.onGoogleClick = { startGoogleSignInActivity(activity, googleApiClient, RC_SIGN_IN) }
    }

    override fun onConnectionFailed(p0: ConnectionResult) = onFailure()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val result = getELPGoogleSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun getGoogleApiClient(activity: FragmentActivity) =
            GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOptions(activity))
                    .build()

    private fun getGoogleSignInOptions(activity: FragmentActivity) =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.server_client_id))
                    .requestEmail()
                    .build()

    private fun handleSignInResult(result: ELPGoogleSignInResult) {
        if (result.isSuccess) {
            val googleToken = result.idToken
            if (googleToken != null) {
                onSuccess(googleToken)
            } else {
                onFailure()
            }
        } else {
            onFailure()
        }
    }
}

typealias OnGoogleClick = () -> Unit