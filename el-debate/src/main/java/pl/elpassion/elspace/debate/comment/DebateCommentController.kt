package pl.elpassion.elspace.debate.comment

class DebateCommentController(private val api: DebateComment.Api) {

    fun sendComment(message: String) {
        api.comment(message)
    }
}