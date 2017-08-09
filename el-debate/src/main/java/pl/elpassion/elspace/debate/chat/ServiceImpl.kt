package pl.elpassion.elspace.debate.chat

import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class ServiceImpl(val api: DebateChat.Api) : DebateChat.Service {

    override fun getLatestComments(token: String): Single<List<Comment>> = api.comment(token)

    override fun getNewComment(debateCode: String): PublishSubject<Comment> = PublishSubject.create()

    override fun sendComment(commentToSend: CommentToSend) = commentToSend.run { api.comment(token, message, firstName, lastName) }
}