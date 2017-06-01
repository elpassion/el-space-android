package pl.elpassion.elspace.hub.login.instant

class InstantGoogleHubLoginController(
        val view: InstantGoogleHubLogin.View,
        val repository: InstantGoogleHubLogin.Repository) {

    fun onCreate() {
        if (repository.readToken() != null) {
            view.openOnLoggedInScreen()
        } else {
            view.startGoogleLoginIntent()
        }
    }
}