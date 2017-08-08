package pl.elpassion.elspace.debate.chat

import io.reactivex.Single
import io.reactivex.subjects.SingleSubject

class ServiceImpl(val api: DebateChat.Api) : DebateChat.Service {

    override fun sendComment(commentToSend: CommentToSend) = commentToSend.run { api.comment(token, message, firstName, lastName) }

    override fun getLatestComments(token: String): Single<List<Comment>> = SingleSubject.create()
}