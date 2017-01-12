package pl.elpassion.space.pacman

import org.junit.Test
import pl.elpassion.space.pacman.WebSocketClient.Event
import pl.elpassion.space.pacman.WebSocketClient.Event.*
import rx.observers.TestSubscriber
import rx.subjects.PublishSubject

class RxUtilsTest {

    val eventS: PublishSubject<Event> = PublishSubject.create()
    val messageS = eventS.asMessageS()

    val subscriber: TestSubscriber<String> = TestSubscriber.create()

    val exception = IllegalStateException("Test exception")

    init {
        messageS.subscribe(subscriber)
    }

    @Test
    fun shouldEmitAllMessages() {
        eventS.onNext(Opened(), Message("a"), Message("b"), Message("c"))
        subscriber.assertValues("a", "b", "c")
    }

    @Test
    fun shouldCompleteWhenWebSocketClosed() {
        eventS.onNext(Opened(), Message("a"), Message("b"), Message("c"), Closed(), Message("X"))
        subscriber.assertValues("a", "b", "c")
        subscriber.assertCompleted()
    }


    @Test
    fun shouldIgnoreEventsBeforeWebSocketOpened() {
        eventS.onNext(Message("X"), Message("Y"), Opened(), Message("a"), Message("b"), Message("c"))
        subscriber.assertValues("a", "b", "c")
        subscriber.assertNoTerminalEvent()
    }

    @Test
    fun shouldTerminateWithErrorWhenFailed() {
        eventS.onNext(Opened(), Message("a"), Message("b"), Failed(exception), Message("c"))
        subscriber.assertValues("a", "b")
        subscriber.assertError(exception)
    }
}