package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.api.UnauthenticatedRetrofitProvider
import pl.elpassion.elspace.common.Provider

object LoginHubTokenApiProvider : Provider<Login.HubTokenApi>({
    UnauthenticatedRetrofitProvider.get().create(Login.HubTokenApi::class.java)
})