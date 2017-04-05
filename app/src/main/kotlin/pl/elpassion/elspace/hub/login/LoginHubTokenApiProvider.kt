package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider

object LoginHubTokenApiProvider : Provider<Login.HubTokenApi>({
    RetrofitProvider.get().create(Login.HubTokenApi::class.java)
})