package pl.elpassion.space.pacman

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.Closeable


class WebSocketClientApiImpl : WebSocketClient.Api, Closeable {

    private val okHttpClient by lazy { OkHttpClient() }
    lateinit var webSocket: WebSocket

    override fun connect(url: String, listener: WebSocketListener) {
        webSocket = okHttpClient.newWebSocket(createRequest(url), listener)
    }

    override fun close() {
        webSocket.close(0, null)
    }

    private fun createRequest(url: String) = Request.Builder().url(url) .build()
}