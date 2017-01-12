package pl.elpassion.space.pacman

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener


class WebSocketClientApiImpl : WebSocketClient.Api {

    val okHttpClient by lazy { OkHttpClient() }

    override fun connect(url: String, listener: WebSocketListener) {
        okHttpClient.newWebSocket(createRequest(url), listener)
    }

    override fun close() {
    }

    private fun createRequest(url: String): Request? {
        return Request.Builder().url(url) .build()
    }
}