package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.model.IndoorwayPosition
import pl.elpassion.space.pacman.model.MapObject
import rx.Completable
import rx.Observable

interface PacMan {
    interface MapView {
        fun loadMap(): Completable
        fun initTextures()
        fun updatePosition(position: IndoorwayPosition)
    }

    interface View {
        fun showMapLoadingError()
        fun updatePosition(position: IndoorwayPosition)
        fun handleMissingPermissionException(permission: String)
        fun handleBLENotSupportedException()
        fun handleBluetoothDisabledException()
        fun handleLocationDisabledException()
        fun addMapObjects(mapObjects: List<MapObject>)
        fun showPlayersUpdateError()
        fun removeMapObjects(mapObjects: List<MapObject>)
    }

    interface PositionService {
        fun start(): Observable<IndoorwayPosition>
    }

    interface PlayersService {
        fun getPlayers(): Observable<List<MapObject>>
        fun close()
        fun send(player: String, it: IndoorwayPosition)
    }
}