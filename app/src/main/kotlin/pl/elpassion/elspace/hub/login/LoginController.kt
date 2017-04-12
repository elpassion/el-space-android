package pl.elpassion.elspace.hub.login

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class LoginController(private val view: Login.View,
                      private val loginRepository: Login.Repository,
                      private val shortcutService: ShortcutService,
                      private val api: Login.HubTokenApi,
                      private val schedulersSupplier: SchedulersSupplier) {

    private val subscriptions = CompositeDisposable()

    fun onCreate() {
        if (loginRepository.readToken() != null) {
            addShortcutsIfSupported()
            view.openReportListScreen()
        }
    }

    fun onGoogleToken(googleToken: String) {
        view.showLoader()
        api.loginWithGoogleToken(GoogleTokenForHubTokenApi(googleToken))
                .supplySchedulers()
                .doOnDispose { view.hideLoader() }
                .subscribe({
                    onCorrectHubToken(it.accessToken)
                }, {
                    view.showGoogleTokenError()
                })
                .addTo(subscriptions)
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    fun onLogin(token: String) {
        if (token.isNotEmpty()) {
            onCorrectHubToken(token)
        } else {
            view.showEmptyLoginError()
        }
    }

    fun onHub() {
        view.openHubWebsite()
    }

    private fun onCorrectHubToken(token: String) {
        loginRepository.saveToken(token)
        view.openReportListScreen()
        addShortcutsIfSupported()
    }

    private fun addShortcutsIfSupported() {
        if (shortcutService.isSupportingShortcuts()) {
            shortcutService.creteAppShortcuts()
        }
    }

    private fun <T> Observable<T>.supplySchedulers() = this
            .subscribeOn(schedulersSupplier.subscribeOn)
            .observeOn(schedulersSupplier.observeOn)
}