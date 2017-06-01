package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient

object GoogleSingInDI {
    var startGoogleSignInActivity: (Activity, () -> GoogleApiClient, Int) -> Unit = { activity, googleApiClient, requestCode ->
        activity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient()), requestCode)
    }

    var getHubGoogleSignInResult: (Intent?) -> GoogleHubLogin.HubGoogleSignInResult = {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it)
        GoogleHubLogin.HubGoogleSignInResult(result.isSuccess, result.signInAccount?.idToken)
    }

    var logoutFromGoogle: (() -> GoogleApiClient) -> Unit = {
        Auth.GoogleSignInApi.signOut(it())
    }
}