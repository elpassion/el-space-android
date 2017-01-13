package pl.elpassion.space.pacman.api

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.elpassion.space.SubscriptionSubjectVerifier
import pl.elpassion.space.pacman.json.stringFromFile
import pl.elpassion.space.pacman.model.MapObject
import pl.elpassion.space.pacman.model.Position
import rx.observers.TestSubscriber

class PlayersServiceImplTest {

    private val eventsSubject = SubscriptionSubjectVerifier<WebSocketClientImpl.Event>()
    private val webSocket = mock<WebSocketClient>().apply {
        whenever(this.connect()).thenReturn(eventsSubject.observable)
    }
    private val subscriber = TestSubscriber.create<List<MapObject>>()
    private val playersList = listOf(MapObject("unique_string_player_id", Position(52.0, 21.0)))
    private val playersService = PlayersServiceImpl(webSocket)

    @Before
    fun setUp() {
        playersService.getPlayers().subscribe(subscriber)
    }

    @Test
    fun shouldOpenConnection() {
        verify(webSocket).connect()
    }

    @Test
    fun shouldForwardEvents() {
        eventsSubject.onNext(WebSocketClientImpl.Event.Opened())
        eventsSubject.onNext(WebSocketClientImpl.Event.Message(stringFromFile("location-update.json")))
        subscriber.assertValue(playersList)
    }

    @Test
    fun shouldNotEmitEventsUntilTheyAppear() {
        subscriber.assertNoValues()
    }

    @Test
    fun shouldForwardErrors() {
        eventsSubject.onNext(WebSocketClientImpl.Event.Opened())
        eventsSubject.onNext(WebSocketClientImpl.Event.Failed(RuntimeException()))
        assertTrue(subscriber.onErrorEvents.isNotEmpty())
    }

    @Test
    fun shouldCloseSocket() {
        playersService.close()
        verify(webSocket).close()
    }
}


