package pl.elpassion.space.pacman

import org.junit.Test
import rx.observers.TestSubscriber

class WebSocketClientTest {

    val subscriber = TestSubscriber<String>()

    @Test
    fun shouldSubscribeToMessages() {
        val client = WebSocketClient("")
        client.messages.subscribe(subscriber)
    }
}