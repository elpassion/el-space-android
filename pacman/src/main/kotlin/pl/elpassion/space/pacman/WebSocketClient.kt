package pl.elpassion.space.pacman

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import rx.Observable
import rx.subjects.PublishSubject

class WebSocketClient(private val api: Api, private val url: String) {

    val subject = PublishSubject.create<Event>()

    fun connect(): Observable<Event> {
        api.connect("", object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                subject.onNext(Event.Opened())
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                subject.onNext(Event.Failed())
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            }
        })
        return subject
    }

    sealed class Event {
        class Opened : Event()
        class Failed : Event()
    }

    interface Api {
        fun connect(url: String, listener: WebSocketListener)
    }

}