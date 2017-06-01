package pl.elpassion.elspace.hub.login.instant

class InstantGoogleHubLoginController(val view: InstantGoogleHubLogin.View) {

    fun onCreate() {
        view.openOnLoggedInScreen()
    }
}