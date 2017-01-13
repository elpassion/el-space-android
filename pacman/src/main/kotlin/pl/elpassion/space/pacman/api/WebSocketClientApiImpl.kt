package pl.elpassion.space.pacman.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.Closeable


class WebSocketClientApiImpl : WebSocketClient.Api, Closeable {

    private val okHttpClient by lazy { OkHttpClient() }
    private var webSocket: WebSocket? = null

    override fun connect(url: String, listener: WebSocketListener) {
        webSocket?.let { close() }
        webSocket = okHttpClient.newWebSocket(createRequest(url), listener)
    }

    override fun send(message: String) {
        webSocket?.send(message) ?: throw IllegalStateException("Web socket not connected")
    }

    override fun close() {
        webSocket?.close(0, null)
        webSocket = null
    }

    private fun createRequest(url: String) = Request.Builder().url(url).build()
}