package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.hub.UnauthenticatedRetrofitProvider
import pl.elpassion.elspace.common.Provider

object HubLoginTokenApiProvider : Provider<HubLogin.TokenApi>({
    UnauthenticatedRetrofitProvider.get().create(HubLogin.TokenApi::class.java)
})