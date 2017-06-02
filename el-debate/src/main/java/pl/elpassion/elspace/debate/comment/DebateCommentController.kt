package pl.elpassion.elspace.debate.comment

import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier

class DebateCommentController(
        private val view: DebateComment.View,
        private val api: DebateComment.Api,
        private val schedulers: SchedulersSupplier) {

    private var subscription: Disposable? = null

    fun sendComment(message: String) {
        subscription = api.comment(message)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::showSendCommentSuccess, view::showSendCommentError)
    }

    fun onDestroy() {
        subscription?.dispose()
    }
}