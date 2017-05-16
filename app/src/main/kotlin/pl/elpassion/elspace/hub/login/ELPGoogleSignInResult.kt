package pl.elpassion.elspace.hub.login

interface ELPGoogleSignInResult {
    val isSuccess: Boolean
    val idToken: String?
}