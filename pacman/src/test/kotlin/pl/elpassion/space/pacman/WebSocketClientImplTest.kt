package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener
import org.junit.Test
import pl.elpassion.space.pacman.api.WebSocketClientImpl
import pl.elpassion.space.pacman.api.WebSocketClientImpl.Event
import pl.elpassion.space.pacman.api.WebSocketClientImpl.Event.*
import rx.observers.TestSubscriber
import kotlin.properties.Delegates

class WebSocketClientImplTest {

    val subscriber = TestSubscriber<Event>()
    val api = mock<WebSocketClientImpl.Api>()

    @Test
    fun shouldConnectToApi() {
        val client = WebSocketClientImpl("", api)
        client.connect().subscribe()
        verify(api).connect(any(), any())
    }

    @Test
    fun shouldEmitOpenedEventOnOpen() {
        val stub = ApiStub()
        val client = WebSocketClientImpl("", stub)
        client.connect().subscribe(subscriber)
        stub.listener.onOpen(mock(), createResponseStub())
        subscriber.assertValueThat { it is Opened }
    }

    @Test
    fun shouldEmitFailedEventOnApiFailure() {
        val stub = ApiStub()
        val client = WebSocketClientImpl("", stub)
        client.connect().subscribe(subscriber)
        stub.listener.onFailure(mock(), IllegalStateException(), createResponseStub())
        subscriber.assertValueThat { it is Failed && it.throwable is IllegalStateException }
    }

    @Test
    fun shouldEmitMessageEventOnMessage() {
        val stub = ApiStub()
        val client = WebSocketClientImpl("", stub)
        client.connect().subscribe(subscriber)
        stub.listener.onMessage(mock(), stubMessage)
        subscriber.assertValueThat { it is Message && it.body == stubMessage }
    }

    @Test
    fun shouldEmitClosedEventOnApiClose() {
        val stub = ApiStub()
        val client = WebSocketClientImpl("", stub)
        client.connect().subscribe(subscriber)
        stub.listener.onClosed(mock(),0, "")
        subscriber.assertValueThat { it is Closed }
    }

    @Test
    fun shouldCloseWebSocketOnClose() {
        val client = WebSocketClientImpl("", api)
        client.close()
        verify(api).close()
    }

    @Test
    fun shouldSendMessageToApi() {
        val client = WebSocketClientImpl("", api)
        client.send(Message(stubMessage))
        verify(api).send(stubMessage)
    }

//    @Ignore
    @Test
    fun shouldConnectPlayer1ToRealApi() {
        val client = WebSocketClientImpl("ws://192.168.1.19:8181/ws")
        client.connect().subscribe {
            println(when (it) {
                is Message -> "Message: ${it.body}"
                else -> it.javaClass.simpleName
            })
            //client.send(Message("{\"event\": \"location_update\", \"player_id\": \"Player1\", \"latitude\": 53.0, \"longitude\": 21.0}"))
        }
        Thread.sleep(60000)
    }

    private val stubMessage = "id: 0, lat: 0, long: 0"

    private fun createResponseStub() = Response.Builder()
            .request(createRequestStub())
            .protocol(Protocol.HTTP_2)
            .code(200)
            .build()

    private fun createRequestStub() = Request.Builder()
            .url("ws://192.168.1.19:8080/ws")
            .build()

    class ApiStub : WebSocketClientImpl.Api {

        var listener : WebSocketListener by Delegates.notNull<WebSocketListener>()

        override fun connect(url: String, listener: WebSocketListener) {
            this.listener = listener
        }

        override fun send(message: String) { }

        override fun close() { }
    }
}