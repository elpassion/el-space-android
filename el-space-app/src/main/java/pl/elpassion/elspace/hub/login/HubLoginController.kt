package pl.elpassion.elspace.hub.login

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class HubLoginController(private val view: HubLogin.View,
                         private val loginRepository: HubLogin.Repository,
                         private val shortcutService: ShortcutService,
                         private val api: HubLogin.TokenApi,
                         private val schedulersSupplier: SchedulersSupplier) {

    private val subscriptions = CompositeDisposable()

    fun onCreate() {
        if (loginRepository.readToken() != null) {
            addShortcutsIfSupported()
            view.openReportListScreen()
        }
    }

    private fun addShortcutsIfSupported() {
        if (shortcutService.isSupportingShortcuts()) {
            shortcutService.creteAppShortcuts()
        }
    }

    fun onHub() {
        view.openHubWebsite()
    }

    fun onLogin(token: String) {
        if (token.isNotEmpty()) {
            onCorrectHubToken(token)
        } else {
            view.showEmptyLoginError()
        }
    }

    fun onGoogleSignInResult(googleSignInResult: ELPGoogleSignInResult) {
        when {
            googleSignInResult.isSuccess && googleSignInResult.idToken != null -> onGoogleToken(googleSignInResult.idToken!!)
            else -> view.showGoogleTokenError()
        }
    }

    private fun onGoogleToken(googleToken: String) {
        view.showLoader()
        api.loginWithGoogleToken(GoogleTokenForHubTokenApi(googleToken))
                .supplySchedulers()
                .doFinally { view.hideLoader() }
                .subscribe({
                    onCorrectHubToken(it.accessToken)
                }, {
                    view.showGoogleTokenError()
                })
                .addTo(subscriptions)
    }

    private fun onCorrectHubToken(token: String) {
        loginRepository.saveToken(token)
        view.openReportListScreen()
        addShortcutsIfSupported()
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun <T> Observable<T>.supplySchedulers() = this
            .subscribeOn(schedulersSupplier.backgroundScheduler)
            .observeOn(schedulersSupplier.uiScheduler)

}