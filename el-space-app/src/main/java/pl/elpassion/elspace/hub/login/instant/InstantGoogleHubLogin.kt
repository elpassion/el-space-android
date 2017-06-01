package pl.elpassion.elspace.hub.login.instant

interface InstantGoogleHubLogin {
    interface View {
        fun openOnLoggedInScreen()
        fun startGoogleLoginIntent()
    }

    interface Repository {
        fun  readToken(): String?
    }
}