package pl.elpassion.elspace.hub.login

import com.google.android.gms.auth.api.signin.GoogleSignInResult

class ELPGoogleSignInResultImpl(private val googleSignInResult: GoogleSignInResult) : ELPGoogleSignInResult {

    override val isSuccess: Boolean get() {
        return googleSignInResult.isSuccess
    }
    override val idToken: String? get() {
        return googleSignInResult.signInAccount?.idToken
    }

}