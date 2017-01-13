package pl.elpassion.space.pacman.api

import pl.elpassion.space.pacman.PacMan
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.model.Position
import rx.Observable

class PlayersServiceImpl(val webSocket: WebSocketClient) : PacMan.PlayersService {
    override fun getPlayers(): Observable<List<Player>> {
        return webSocket.connect()
                .asMessageS()
                .deserialize()
                .map { it.map { Player(it.playerId, Position(it.latitude, it.longitude)) } }
    }

    override fun close() {
        webSocket.close()
    }
}