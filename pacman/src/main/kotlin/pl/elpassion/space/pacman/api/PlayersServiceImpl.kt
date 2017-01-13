package pl.elpassion.space.pacman.api

import com.indoorway.android.common.sdk.model.IndoorwayPosition
import pl.elpassion.space.pacman.PacMan
import pl.elpassion.space.pacman.model.LocationUpdateEvent
import pl.elpassion.space.pacman.model.MapObject
import pl.elpassion.space.pacman.model.Position
import rx.Observable
import java.util.concurrent.TimeUnit

class PlayersServiceImpl(val webSocket: WebSocketClient) : PacMan.PlayersService {
    override fun getPlayers(): Observable<List<MapObject>> {
        return webSocket.connect()
                .asMessageS()
                .sample(1, TimeUnit.SECONDS)
                .deserialize()
                .map { it.map { it.toMapObject() } }
    }

    override fun send(player: String, it: IndoorwayPosition) {
        webSocket.send(WebSocketClientImpl.Event.Message("{\"player_id\": \"$player\", \"latitude\": ${it.coordinates.latitude}, \"longitude\": ${it.coordinates.longitude}}"))
    }

    override fun close() {
        webSocket.close()
    }
}

private fun LocationUpdateEvent.toMapObject(): MapObject {
    return MapObject(this.player, Position(this.latitude, this.longitude), this.type.toMapObjectType())
}

private fun LocationUpdateEvent.Type.toMapObjectType(): MapObject.Type {
    return when (this) {
        LocationUpdateEvent.Type.GHOST -> MapObject.Type.GHOST
        LocationUpdateEvent.Type.FOOD -> MapObject.Type.FOOD
        LocationUpdateEvent.Type.PLAYER -> MapObject.Type.PLAYER
    }
}
