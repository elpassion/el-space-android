package pl.elpassion.debate.login

import pl.elpassion.debate.DebateTokenRepository
import rx.Subscription

class DebateLoginController(
        private val view: DebateLogin.View,
        private val tokenRepo: DebateTokenRepository,
        private val loginApi: DebateLogin.Api) {

    private var subscription: Subscription? = null

    fun onLogToDebate(debateCode: String) {
        subscription = loginApi.login(debateCode)
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .doOnNext { tokenRepo.saveToken(it.authToken) }
                .subscribe({
                    view.openDebateScreen()
                }, {
                    view.showLoginFailedError()
                })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }
}