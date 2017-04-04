package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.common.Provider
import rx.Observable

object LoginHubTokenApiProvider : Provider<Login.HubTokenApi>({
    object : Login.HubTokenApi {
        override fun loginWithGoogleToken(): Observable<String> = Observable.empty()
    }
})