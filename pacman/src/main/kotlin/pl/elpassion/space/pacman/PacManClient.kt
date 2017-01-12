package pl.elpassion.space.pacman

import okhttp3.*
import okio.ByteString

class PacManClient {

    private val client = OkHttpClient()

    fun connect() {
        val request = Request.Builder().url("url").build()
        val webSocket = client.newWebSocket(request, object : WebSocketListener() {
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
    }
}