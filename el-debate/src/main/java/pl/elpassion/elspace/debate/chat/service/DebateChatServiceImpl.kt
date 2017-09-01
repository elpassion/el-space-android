package pl.elpassion.elspace.debate.chat.service

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend
import pl.elpassion.elspace.debate.chat.DebateChat
import pl.elpassion.elspace.debate.chat.InitialsComments

class DebateChatServiceImpl(private val api: DebateChat.Api, private val socket: DebateChat.Socket) : DebateChat.Service {

    override fun initialsCommentsObservable(token: String): Single<InitialsComments> =
            api.comment(token)
                    .map { it.copy(comments = it.comments.sortedBy { it.createdAt }) }

    override fun liveCommentsObservable(debateCode: String): Observable<Comment> = socket.commentsObservable(debateCode)

    override fun sendComment(commentToSend: CommentToSend): Completable =
            commentToSend.run { api.comment(token, message, firstName, lastName) }
}