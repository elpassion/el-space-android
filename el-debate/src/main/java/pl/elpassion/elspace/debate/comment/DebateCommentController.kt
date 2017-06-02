package pl.elpassion.elspace.debate.comment

import pl.elpassion.elspace.common.SchedulersSupplier

class DebateCommentController(
        private val view: DebateComment.View,
        private val api: DebateComment.Api,
        private val schedulers: SchedulersSupplier) {

    fun sendComment(message: String) {
        api.comment(message)
                .subscribeOn(schedulers.backgroundScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe()
    }
}