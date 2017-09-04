package pl.elpassion.elspace.debate.chat.service

import io.reactivex.Observable
import io.reactivex.Single
import pl.elpassion.elspace.debate.chat.*

class DebateChatServiceImpl(private val api: DebateChat.Api, private val socket: DebateChat.Socket) : DebateChat.Service {

    override fun initialsCommentsObservable(token: String): Single<InitialsComments> =
            api.comment(token)
                    .map { it.copy(comments = it.comments.sortedBy { it.createdAt }) }

    override fun liveCommentsObservable(debateCode: String): Observable<Comment> = socket.commentsObservable(debateCode)

    override fun sendComment(commentToSend: CommentToSend): Single<SendCommentResponse> =
            commentToSend.run { api.comment(token, message, firstName, lastName) }
}