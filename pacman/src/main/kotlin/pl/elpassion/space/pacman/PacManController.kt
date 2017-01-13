package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import pl.elpassion.space.pacman.utils.completeOnError
import pl.elpassion.space.pacman.utils.save
import rx.subscriptions.CompositeSubscription

class PacManController(val view: PacMan.View, val mapView: PacMan.MapView, val positionService: PacMan.PositionService, val playersService: PacMan.PlayersService) {

    private val compositeSubscription: CompositeSubscription = CompositeSubscription()

    fun onCreate() {
        mapView.loadMap()
                .doOnCompleted { mapView.initTextures() }
                .andThen(
                        playersService.getPlayers()
                                .doOnNext { view.updatePlayers(it) }
                                .completeOnError { view.showPlayersUpdateError() })
                .subscribe({}, {
                    view.showMapLoadingError()
                })
                .save(to = compositeSubscription)
    }

    fun onResume() {
        positionService.start().subscribe({
            mapView.updatePosition(it)
            view.updatePosition(it)
        }, {
            when (it) {
                is MissingPermissionException -> view.handleMissingPermissionException(it.permission)
                is BLENotSupportedException -> view.handleBLENotSupportedException()
                is BluetoothDisabledException -> view.handleBluetoothDisabledException()
                is LocationDisabledException -> view.handleLocationDisabledException()
            }
        }).save(to = compositeSubscription)
    }

    fun onPause() {
        compositeSubscription.clear()
    }
}