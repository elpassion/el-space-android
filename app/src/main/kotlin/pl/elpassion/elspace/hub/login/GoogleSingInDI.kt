package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient

object GoogleSingInDI {
    var startGoogleSignInActivity: (Activity, GoogleApiClient, Int) -> Unit = { activity, googleApiClient, requestCode ->
        activity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), requestCode)
    }

    var getELPGoogleSignInResultFromIntent: (Intent?) -> ELPGoogleSignInResult = {
        ELPGoogleSignInResultImpl(Auth.GoogleSignInApi.getSignInResultFromIntent(it))
    }

}