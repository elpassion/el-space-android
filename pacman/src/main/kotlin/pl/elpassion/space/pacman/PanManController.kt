package pl.elpassion.space.pacman

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
        subscription = positionService.start().subscribe {
            view.updatePosition(it)
        }
    }

    fun onPause() {
        subscription?.unsubscribe()
    }
}