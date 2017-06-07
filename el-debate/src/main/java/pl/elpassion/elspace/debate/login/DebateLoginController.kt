package pl.elpassion.elspace.debate.login

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebateTokenRepository

class DebateLoginController(
        private val view: DebateLogin.View,
        private val tokenRepo: DebateTokenRepository,
        private val loginApi: DebateLogin.Api,
        private val schedulers: SchedulersSupplier) {

    private var subscription: Disposable? = null

    fun onLogToDebate(debateCode: String) {
        if (debateCode.length != 5) {
            view.showWrongPinError()
        } else {
            makeSubscription(debateCode)
        }
    }

    private fun makeSubscription(debateCode: String) {
        tokenRepo.saveLatestDebateCode(debateCode)
        subscription = getAuthTokenObservable(debateCode)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally { view.hideLoader() }
                .subscribe({
                    view.openDebateScreen(it)
                }, {
                    view.showLoginFailedError()
                })
    }

    private fun getAuthTokenObservable(debateCode: String) =
            if (tokenRepo.hasToken(debateCode)) {
                Single.just(tokenRepo.getTokenForDebate(debateCode))
            } else {
                loginApi.login(debateCode)
                        .map { it.authToken }
                        .doOnSuccess { tokenRepo.saveDebateToken(debateCode = debateCode, authToken = it) }
            }

    fun onDestroy() {
        subscription?.dispose()
    }
}