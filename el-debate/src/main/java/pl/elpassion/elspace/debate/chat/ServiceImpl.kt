package pl.elpassion.elspace.debate.chat

import io.reactivex.Observable
import io.reactivex.Single

class ServiceImpl(val api: DebateChat.Api) : DebateChat.Service {

    override fun getLatestComments(token: String): Single<List<Comment>> = api.comment(token)

    override fun getNewComment(debateCode: String): Observable<Comment> = Observable.create<Comment>{}

    override fun sendComment(commentToSend: CommentToSend) = commentToSend.run { api.comment(token, message, firstName, lastName) }
}