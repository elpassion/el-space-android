package pl.elpassion.elspace.debate.login

import pl.elpassion.elspace.debate.DebateTokenRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class DebateLoginController(
        private val view: DebateLogin.View,
        private val tokenRepo: DebateTokenRepository,
        private val loginApi: DebateLogin.Api) {

    private var subscription: Disposable? = null

    fun onLogToDebate(debateCode: String) {
        if (debateCode.length != 5) {
            view.showWrongPinError()
        } else {
            makeSubscription(debateCode)
        }
    }

    private fun makeSubscription(debateCode: String) {
        subscription = getAuthTokenObservable(debateCode)
                .doOnSubscribe { view.showLoader() }
                .doOnDispose { view.hideLoader() }
                .subscribe({
                    view.openDebateScreen(it)
                }, {
                    view.showLoginFailedError()
                })
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
        subscription?.dispose()
    }
}