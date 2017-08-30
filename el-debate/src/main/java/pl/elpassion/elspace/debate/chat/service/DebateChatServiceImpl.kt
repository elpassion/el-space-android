package pl.elpassion.elspace.debate.chat.service

import io.reactivex.Completable
import io.reactivex.Observable
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend
import pl.elpassion.elspace.debate.chat.DebateChat

class DebateChatServiceImpl(private val api: DebateChat.Api, private val socket: DebateChat.Socket) : DebateChat.Service {

    override fun initialsCommentsObservable(token: String): Observable<Comment> =
            api.comment(token)
                    .map { it.sortedBy { it.createdAt } }
                    .flattenAsObservable { it }

    override fun liveCommentsObservable(debateCode: String): Observable<Comment> = socket.commentsObservable(debateCode)

    override fun sendComment(commentToSend: CommentToSend): Completable =
            commentToSend.run { api.comment(token, message, firstName, lastName) }
}