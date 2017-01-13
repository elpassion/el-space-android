package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.model.IndoorwayPosition
import pl.elpassion.space.pacman.model.Player
import rx.Observable

interface PacMan {
    interface MapView {
        fun loadMap(): Observable<Unit>
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
        fun updatePlayers(players: List<Player>)
        fun showPlayersUpdateError()
    }

    interface PositionService {
        fun start(): Observable<IndoorwayPosition>
    }

    interface PlayersService {
        fun getPlayers(): Observable<List<Player>>
    }
}