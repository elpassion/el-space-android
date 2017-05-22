package pl.elpassion.elspace.hub.login

data class HubTokenFromApi(val accessToken: String)

data class GoogleTokenForHubTokenApi(val idToken: String)