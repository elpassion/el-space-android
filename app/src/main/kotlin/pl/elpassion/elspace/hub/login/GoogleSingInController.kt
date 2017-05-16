package pl.elpassion.elspace.hub.login

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import pl.elpassion.R
import pl.elpassion.elspace.common.Provider

interface GoogleSingInController {
    fun initializeGoogleSignIn(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun onGoogleSignInClick()
}

object GoogleSingInControllerProvider : Provider<GoogleSingInController>({
    object : GoogleSingInController, GoogleApiClient.OnConnectionFailedListener {

        override fun onGoogleSignInClick() {
            onGoogleClick()
        }

        private val RC_SIGN_IN = 64927
        private lateinit var onSuccess: (String) -> Unit
        private lateinit var onFailure: () -> Unit
        private lateinit var onGoogleClick: OnGoogleClick

        override fun initializeGoogleSignIn(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
            this.onSuccess = onSuccess
            this.onFailure = onFailure
            val googleApiClient = getGoogleApiClient(activity)
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            this.onGoogleClick = { activity.startActivityForResult(signInIntent, RC_SIGN_IN) }
        }

        override fun onConnectionFailed(p0: ConnectionResult) = onFailure()

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == RC_SIGN_IN) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInResult(result)
            }
        }

        private fun getGoogleApiClient(activity: FragmentActivity) =
                GoogleApiClient.Builder(activity)
                        .enableAutoManage(activity, this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOptions(activity))
                        .build()

        private fun getGoogleSignInOptions(activity: FragmentActivity) =
                GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getString(R.string.server_client_id))
                        .requestEmail()
                        .build()

        private fun handleSignInResult(result: GoogleSignInResult) {
            if (result.isSuccess) {
                val googleToken = result.signInAccount?.idToken
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
})

typealias OnGoogleClick = () -> Unit