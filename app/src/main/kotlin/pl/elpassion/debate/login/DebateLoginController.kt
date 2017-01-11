package pl.elpassion.debate.login

import pl.elpassion.debate.DebateTokenRepository
import rx.Observable
import rx.Subscription

class DebateLoginController(
        private val view: DebateLogin.View,
        private val tokenRepo: DebateTokenRepository,
        private val loginApi: DebateLogin.Api) {

    private var subscription: Subscription? = null

    fun onLogToDebate(debateCode: String) {
        if (debateCode.length >= 5) {
            subscription = getAuthTokenObservable(debateCode)
                    .doOnSubscribe { view.showLoader() }
                    .doOnUnsubscribe { view.hideLoader() }
                    .subscribe({
                        view.openDebateScreen(it)
                    }, {
                        view.showLoginFailedError()
                    })
        } else {
            view.showWrongPinError()
        }
    }

    private fun getAuthTokenObservable(debateCode: String) =
            if (tokenRepo.hasToken(debateCode)) {
                Observable.just(tokenRepo.getTokenForDebate(debateCode))
            } else {
                loginApi.login(debateCode)
                        .map { it.authToken }
                        .doOnNext { tokenRepo.saveDebateToken(debateCode = debateCode, authToken = it) }
            }

    fun onDestroy() {
        subscription?.unsubscribe()
    }
}