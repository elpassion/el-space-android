package pl.elpassion.elspace.debate.comment

import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier

class DebateCommentController(
        private val view: DebateComment.View,
        private val api: DebateComment.Api,
        private val schedulers: SchedulersSupplier) {

    private var subscription: Disposable? = null

    fun sendComment(token: String, message: String) {
        if (message.isEmpty()) view.showInvalidInputError() else callApi(token, message)
    }

    private fun callApi(token: String, message: String) {
        subscription = api.comment(token, message, "mrnick")
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::closeScreen, view::showSendCommentError)
    }

    fun onDestroy() {
        subscription?.dispose()
    }

    fun onCancel() {
        view.closeScreen()
    }
}