package pl.elpassion.space.pacman.api

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.space.SubscriptionSubjectVerifier
import pl.elpassion.space.pacman.PacMan
import pl.elpassion.space.pacman.json.stringFromFile
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.model.Position
import rx.Observable
import rx.observers.TestSubscriber

class PlayersServiceImplTest {

    private val eventsSubject = SubscriptionSubjectVerifier<WebSocketClientImpl.Event>()
    private val webSocket = mock<WebSocketClient>().apply {
        whenever(this.connect()).thenReturn(eventsSubject.observable)
    }
    private val subscriber = TestSubscriber.create<List<Player>>()
    private val playersList = listOf(Player("unique_string_player_id", Position(52.0, 21.0)))

    @Before
    fun setUp() {
        PlayersServiceImpl(webSocket).getPlayers().subscribe(subscriber)
    }

    @Test
    fun shouldOpenConnection() {
        verify(webSocket).connect()
    }

    @Test
    fun shouldForwardEvents() {
        eventsSubject.onNext(WebSocketClientImpl.Event.Message(stringFromFile("location-update.json")))
        subscriber.assertValue(playersList)
    }

    @Test
    fun shouldNotEmitEventsUntilTheyAppear() {
        subscriber.assertNoValues()
    }
}


class PlayersServiceImpl(val webSocket: WebSocketClient) : PacMan.PlayersService {

    override fun getPlayers(): Observable<List<Player>> {

        return webSocket.connect()
                .ofType(WebSocketClientImpl.Event.Message::class.java)
                .map { it.body }
                .deserialize()
                .map { it.map { Player(it.playerId, Position(it.latitude, it.longitude)) } }
    }
}
