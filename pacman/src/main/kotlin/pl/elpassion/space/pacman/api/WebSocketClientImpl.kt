package pl.elpassion.space.pacman.api

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import rx.AsyncEmitter
import rx.Observable
import java.io.Closeable

class WebSocketClientImpl(private val url: String, private val api: Api = WebSocketClientApiImpl()) : Closeable, WebSocketClient {

    override fun connect(): Observable<Event> {
        return Observable.fromAsync({ emitter ->
            api.connect(url, WebSocketListenerImpl(emitter))
        }, AsyncEmitter.BackpressureMode.BUFFER)
    }

    override fun send(message: Event.Message) {
        api.send(message.body)
    }

    override fun close() {
        api.close()
    }


    class WebSocketListenerImpl(val emitter: AsyncEmitter<Event>) : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            emitter.onNext(Event.Opened())
        }

        override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
            emitter.onNext(Event.Failed(throwable))
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            emitter.onNext(Event.Message(text))
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            emitter.onNext(Event.Message(bytes.utf8()))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            emitter.onNext(Event.Closed())
        }
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
    fun send(message: WebSocketClientImpl.Event.Message)
    fun close()
}
