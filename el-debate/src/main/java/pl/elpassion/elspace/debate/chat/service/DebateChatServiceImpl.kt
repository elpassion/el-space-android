package pl.elpassion.elspace.debate.chat.service

import io.reactivex.Observable
import io.reactivex.Single
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend
import pl.elpassion.elspace.debate.chat.DebateChat
import pl.elpassion.elspace.debate.chat.InitialsComments

class DebateChatServiceImpl(private val api: DebateChat.Api, private val socket: DebateChat.Socket) : DebateChat.Service {

    override fun initialsCommentsObservable(token: String, nextPosition: Long?): Single<InitialsComments> =
            if (nextPosition == null) {
                api.getComments(token)
            } else {
                api.getNextComments(token, nextPosition)
            }.map {
                it.comments.forEach { it.wasShown = true }
                it.copy(comments = it.comments.sortedBy { it.id })
            }

    override fun liveCommentsObservable(debateCode: String, userId: Long): Observable<Comment> = socket.commentsObservable(debateCode, userId)

    override fun sendComment(commentToSend: CommentToSend): Single<Comment> =
            commentToSend.run { api.comment(token, message, firstName, lastName) }
}