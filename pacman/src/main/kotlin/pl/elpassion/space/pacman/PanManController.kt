package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import rx.Subscription

class PanManController(val view: PacMan.View, val mapView: PacMan.MapView, val positionService: PacMan.PositionService) {
    private var subscription: Subscription? = null

    fun onCreate() {
        mapView.loadMap().subscribe({
            mapView.initTextures()
        }, {
            view.showMapLoadingError()
        })
    }

    fun onResume() {
        subscription = positionService.start().subscribe({
            view.updatePosition(it)
        }, {
            when(it){
                is MissingPermissionException -> view.handleMissingPermissionException(it.permission)
                is BLENotSupportedException -> view.handleBLENotSupportedException()
            }
        })
    }

    fun onPause() {
        subscription?.unsubscribe()
    }
}