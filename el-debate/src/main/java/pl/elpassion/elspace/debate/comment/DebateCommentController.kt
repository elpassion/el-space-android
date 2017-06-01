package pl.elpassion.elspace.debate.comment

class DebateCommentController(
        private val view: DebateComment.View,
        private val api: DebateComment.Api) {

    fun sendComment(message: String) {
        api.comment(message)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe()
    }
}