package pl.elpassion.debate.login

import pl.elpassion.debate.DebateTokenRepository
import rx.Subscription

class DebateLoginController(
        private val view: DebateLogin.View,
        private val tokenRepo: DebateTokenRepository,
        private val loginApi: DebateLogin.Api) {

    private var subscription: Subscription? = null

    fun onLogToDebate(debateCode: String) {
        view.showLoader()
        if (tokenRepo.hasToken(debateCode)) {
            view.openDebateScreen(tokenRepo.getTokenForDebate(debateCode))
        } else {
            subscription = loginApi.login(debateCode)
                    .doOnUnsubscribe { view.hideLoader() }
                    .doOnNext { tokenRepo.saveDebateToken(debateCode = debateCode, authToken = it.authToken) }
                    .subscribe({
                        view.openDebateScreen(it.authToken)
                    }, {
                        view.showLoginFailedError()
                    })
        }
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }
}