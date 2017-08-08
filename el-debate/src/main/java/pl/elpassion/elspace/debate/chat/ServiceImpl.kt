package pl.elpassion.elspace.debate.chat

import io.reactivex.Single

class ServiceImpl(val api: DebateChat.Api) : DebateChat.Service {

    override fun sendComment(commentToSend: CommentToSend) = commentToSend.run { api.comment(token, message, firstName, lastName) }

    override fun getLatestComments(token: String): Single<List<Comment>> = api.comment(token)
}