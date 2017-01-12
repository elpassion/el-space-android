package pl.elpassion.space.pacman

class PanManController(val view: PacMan.View, val mapView: PacMan.MapView, val positionService: PacMan.PositionService) {
    fun onCreate() {
        mapView.loadMap().subscribe({
            mapView.initTextures()
        }, {
            view.showMapLoadingError()
        })
    }

    fun onResume() {
        positionService.start().subscribe {
            view.updatePosition(it)
        }
    }

    fun onPause() {
        positionService.stop()
    }
}