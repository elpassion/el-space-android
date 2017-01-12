package pl.elpassion.space.pacman

import rx.Observable

interface PacMan {
    interface MapView {
        fun loadMap(): Observable<Unit>
        fun initTextures()
    }

    interface View {
        fun showMapLoadingError()
    }

    interface PositionService {
        fun start()
        fun stop()
    }
}