package pl.elpassion.elspace.hub.login.instant

class InstantGoogleHubLoginController(
        val view: InstantGoogleHubLogin.View,
        val repository: InstantGoogleHubLogin.Repository,
        val api: InstantGoogleHubLogin.Api) {

    fun onCreate() {
        if (repository.readToken() != null) {
            view.openOnLoggedInScreen()
        } else {
            view.startGoogleLoginIntent()
        }
    }

    fun onGoogleSignInResult(hubGoogleSignInResult: InstantGoogleHubLogin.HubGoogleSignInResult) {
        if (hubGoogleSignInResult.isSuccess && hubGoogleSignInResult.googleToken != null) {
            api.loginWithGoogle(hubGoogleSignInResult.googleToken)
                    .subscribe({
                        view.openOnLoggedInScreen()
                    }, {
                        view.showApiLoginError()
                    })
        } else {
            view.showGoogleLoginError()
        }
    }
}