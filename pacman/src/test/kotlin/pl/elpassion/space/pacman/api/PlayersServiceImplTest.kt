package pl.elpassion.space.pacman.api

import org.junit.Test
import pl.elpassion.space.pacman.PacMan
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.model.Position
import rx.Observable
import rx.observers.TestSubscriber

class PlayersServiceImplTest {

    private val subscriber = TestSubscriber.create<List<Player>>()

    private val playersList = listOf(
            Player("1", Position(53.0, 22.0)),
            Player("2", Position(54.0, 23.0))
    )

    @Test
    fun shouldReturnPlayersStream() {
        PlayersServiceImpl().getPlayers().subscribe(subscriber)
        subscriber.assertValues(playersList)
    }
}

class PlayersServiceImpl : PacMan.PlayersService {

    override fun getPlayers(): Observable<List<Player>> {
        return Observable.just(listOf(
                Player("1", Position(53.0, 22.0)),
                Player("2", Position(54.0, 23.0))
        ))
    }
}
