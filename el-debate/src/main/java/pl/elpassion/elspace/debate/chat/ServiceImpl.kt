package pl.elpassion.elspace.debate.chat

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class ServiceImpl(val api: DebateChat.Api) : DebateChat.Service {

    override fun comment(token: String, message: String, firstName: String, lastName: String): Completable =
            api.comment(token, message, firstName, lastName)

    override fun getComments(): Observable<List<String>> = Observable.create<List<String>> { emitter: ObservableEmitter<List<String>> ->

    }
}