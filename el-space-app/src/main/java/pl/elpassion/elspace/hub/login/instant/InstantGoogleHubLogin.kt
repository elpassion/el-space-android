package pl.elpassion.elspace.hub.login.instant

interface InstantGoogleHubLogin {
    interface View {
        fun openOnLoggedInScreen()
    }

    interface Repository {
        fun  readToken(): String?
    }
}