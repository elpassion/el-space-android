package pl.elpassion.space.pacman

class PanManController(val view: PacMan.View, val mapView: PacMan.MapView) {
    fun onCreate() {
        mapView.loadMap().subscribe({
            mapView.initTextures()
        }, {
            view.showMapLoadingError()
        })
    }
}