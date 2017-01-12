package pl.elpassion.space.pacman

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import rx.Observable
import rx.Observable.never
import rx.subjects.BehaviorSubject

class WebSocketClient(private val api: Api, private val url: String) {

    fun connect(): Observable<Connection> {
        api.connect("", object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                super.onOpen(webSocket, response)
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                super.onFailure(webSocket, t, response)
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                super.onClosing(webSocket, code, reason)
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                super.onMessage(webSocket, text)
            }

            override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
                super.onMessage(webSocket, bytes)
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                super.onClosed(webSocket, code, reason)
            }
        })
        return never()
    }

    class Connection {
        val messages = BehaviorSubject.create<String>()
    }

    interface Api {
        fun connect(url: String, listener: WebSocketListener)
    }

}