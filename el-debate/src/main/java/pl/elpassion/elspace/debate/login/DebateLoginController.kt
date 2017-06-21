package pl.elpassion.elspace.debate.login

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository

class DebateLoginController(
        private val view: DebateLogin.View,
        private val debateRepo: DebatesRepository,
        private val loginApi: DebateLogin.Api,
        private val schedulers: SchedulersSupplier) {

    private var subscription: Disposable? = null

    fun onCreate() {
        debateRepo.run {
            getLatestDebateCode()?.let {
                view.fillDebateCode(it)
            }
            getLatestDebateNickname()?.let {
                view.fillDebateNickname(it)
            }
        }
    }

    fun onLogToDebate(debateCode: String, nickname: String) {
        if (debateCode.length != 5) {
            view.showWrongPinError()
        } else if (nickname.isEmpty()) {
            view.showWrongNicknameError()
        } else {
            makeSubscription(debateCode, nickname)
        }
    }

    private fun makeSubscription(debateCode: String, nickname: String) {
        debateRepo.saveLatestDebateCode(debateCode)
        debateRepo.saveLatestDebateNickname(nickname)
        subscription = getAuthTokenObservable(debateCode, nickname)
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

    private fun getAuthTokenObservable(debateCode: String, nickname: String) =
            if (debateRepo.hasToken(debateCode)) {
                Single.just(debateRepo.getTokenForDebate(debateCode))
            } else {
                loginApi.login(debateCode, nickname)
                        .map { it.authToken }
                        .doOnSuccess { debateRepo.saveDebateToken(debateCode = debateCode, authToken = it) }
            }

    fun onDestroy() {
        subscription?.dispose()
    }
}