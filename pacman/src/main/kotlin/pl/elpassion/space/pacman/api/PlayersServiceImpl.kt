package pl.elpassion.space.pacman.api

import pl.elpassion.space.pacman.PacMan
import pl.elpassion.space.pacman.model.LocationUpdateEvent
import pl.elpassion.space.pacman.model.MapObject
import pl.elpassion.space.pacman.model.Position
import rx.Observable

class PlayersServiceImpl(val webSocket: WebSocketClient) : PacMan.PlayersService {
    override fun getPlayers(): Observable<List<MapObject>> {
        return webSocket.connect()
                .asMessageS()
                .deserialize()
                .map { it.map { it.toMapObject() } }
    }

    override fun close() {
        webSocket.close()
    }
}

private fun LocationUpdateEvent.toMapObject(): MapObject {
    return MapObject(this.player, Position(this.latitude, this.longitude), MapObject.Type.GHOST)
}
