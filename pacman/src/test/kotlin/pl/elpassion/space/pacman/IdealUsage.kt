package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import rx.observers.TestSubscriber


class IdealUsage {

    val subscriber = TestSubscriber<String>()
    val api = mock<WebSocketClient.Api>()
    val client = WebSocketClient(api, "")

    @Test
    fun shouldWork() {

        val connectionObservable = client.connect()
        connectionObservable.subscribe { connection ->
            connection.messages.subscribe { message ->


            }
//            connection.send("bla")

        }

    }
}