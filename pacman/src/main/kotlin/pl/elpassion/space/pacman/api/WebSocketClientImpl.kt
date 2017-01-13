package pl.elpassion.space.pacman.api

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import rx.Observable
import rx.subjects.PublishSubject
import java.io.Closeable

class WebSocketClientImpl(private val url: String, private val api: Api = WebSocketClientApiImpl()) : WebSocketListener(), Closeable, WebSocketClient {

    private val subject = PublishSubject.create<Event>()

    override fun connect(): Observable<Event> {
        api.connect(url, this)
        return subject
    }

    fun send(message: Event.Message) {
        api.send(message.body)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        subject.onNext(Event.Opened())
    }

    override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
        subject.onNext(Event.Failed(throwable))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        subject.onNext(Event.Message(text))
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        subject.onNext(Event.Message(bytes.utf8()))
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        subject.onNext(Event.Closed())
    }

    override fun close() {
        api.close()
    }

    sealed class Event {
        class Opened : Event()
        class Closed : Event()
        class Failed(val throwable: Throwable) : Event()
        class Message(val body: String) : Event()
    }

    interface Api {
        fun connect(url: String, listener: WebSocketListener)
        fun send(message: String)
        fun close()
    }
}

interface WebSocketClient {
    fun connect(): Observable<WebSocketClientImpl.Event>
    fun close()
}
