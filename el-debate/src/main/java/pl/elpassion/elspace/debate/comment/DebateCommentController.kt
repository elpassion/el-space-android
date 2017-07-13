package pl.elpassion.elspace.debate.comment

import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository

class DebateCommentController(
        private val view: DebateComment.View,
        private val debateRepo: DebatesRepository,
        private val api: DebateComment.Api,
        private val schedulers: SchedulersSupplier,
        private val maxMessageLength: Int) {

    private var subscription: Disposable? = null

    fun sendComment(token: String, message: String) {
        when {
            message.isBlank() -> view.showInvalidInputError()
            message.length > maxMessageLength -> view.showInputOverLimitError()
            else -> callApi(token, message)
        }
    }

    private fun callApi(token: String, message: String) {
        subscription = api.comment(token, message, "")
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