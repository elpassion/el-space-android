package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import pl.elpassion.elspace.hub.login.instant.InstantGoogleHubLogin

object GoogleSingInDI {
    var startGoogleSignInActivity: (Activity, () -> GoogleApiClient, Int) -> Unit = { activity, googleApiClient, requestCode ->
        activity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient()), requestCode)
    }

    var getHubGoogleSignInResult: (Intent?) -> InstantGoogleHubLogin.HubGoogleSignInResult = {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it)
        InstantGoogleHubLogin.HubGoogleSignInResult(result.isSuccess, result.signInAccount?.idToken)
    }
}