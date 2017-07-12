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
        debateRepo.getLatestDebateCode()?.let {
            view.fillDebateCode(it)
        }
    }

    fun onLogToDebate(debateCode: String) {
        when {
            debateCode.length != 5 -> view.showWrongPinError()
            else -> makeSubscription(debateCode)
        }
    }

    private fun makeSubscription(debateCode: String) {
        debateRepo.saveLatestDebateCode(debateCode)
        subscription = getAuthTokenObservable(debateCode)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::openDebateScreen, this::onLoginError)
    }

    private fun onLoginError(error: Throwable) {
        if (error is HttpException && error.code() == 406) {
            view.showDebateClosedError()
        } else {
            view.showLoginError(error)
        }
    }

    private fun getAuthTokenObservable(debateCode: String) =
            if (debateRepo.hasToken(debateCode)) {
                Single.just(debateRepo.getTokenForDebate(debateCode))
            } else {
                loginApi.login(debateCode)
                        .map { it.authToken }
                        .doOnSuccess { debateRepo.saveDebateToken(debateCode = debateCode, authToken = it) }
            }

    fun onDestroy() {
        subscription?.dispose()
    }
}