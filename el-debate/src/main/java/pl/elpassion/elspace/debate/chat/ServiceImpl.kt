package pl.elpassion.elspace.debate.chat

import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class ServiceImpl(val api: DebateChat.Api) : DebateChat.Service {

    override fun sendComment(commentToSend: CommentToSend) = commentToSend.run { api.comment(token, message, firstName, lastName) }

    override fun getComment(token: String): Observable<GetComment> = Observable.create<GetComment> { emitter: ObservableEmitter<GetComment> ->

    }
}