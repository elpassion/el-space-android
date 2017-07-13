package pl.elpassion.elspace.debate.login

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository
import retrofit2.HttpException

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
        when {
            debateCode.length != 5 -> view.showWrongPinError()
            nickname.isBlank() -> view.showWrongNicknameError()
            else -> makeSubscription(debateCode, nickname)
        }
    }

    private fun makeSubscription(debateCode: String, nickname: String) {
        debateRepo.run {
            saveLatestDebateCode(debateCode)
            saveLatestDebateNickname(nickname)
        }
        subscription = getAuthTokenObservable(debateCode, nickname)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally { view.hideLoader() }
                .subscribe({ view.openDebateScreen(it) }, onLoginError)
    }

    private val onLoginError: (Throwable) -> Unit = { error ->
        if (error is HttpException && error.code() == 406) {
            view.showDebateClosedError()
        } else {
            view.showLoginFailedError(error)
        }
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