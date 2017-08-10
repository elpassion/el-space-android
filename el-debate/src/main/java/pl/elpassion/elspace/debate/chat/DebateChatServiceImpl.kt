package pl.elpassion.elspace.debate.chat

import io.reactivex.Completable
import io.reactivex.Observable

class DebateChatServiceImpl(private val api: DebateChat.Api, private val socket: DebateChat.Socket) : DebateChat.Service {

    override fun commentsObservable(token: String, debateCode: String): Observable<Comment> =
            Observable.concat(api.comment(token).flattenAsObservable { it }, socket.commentsObservable(debateCode))

    override fun sendComment(commentToSend: CommentToSend): Completable =
            commentToSend.run { api.comment(token, message, firstName, lastName) }
}