package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import rx.Observable

interface PacMan {
    interface MapView {
        fun loadMap(): Observable<Unit>
        fun initTextures()
    }

    interface View {
        fun showMapLoadingError()
        fun updatePosition(position: IndoorwayPosition)
        fun handleMissingPermissionException(permission: String)
        fun handleBLENotSupportedException()
        fun handleBluetoothDisabledException()
    }

    interface PositionService {
        fun start(): Observable<IndoorwayPosition>
    }
}