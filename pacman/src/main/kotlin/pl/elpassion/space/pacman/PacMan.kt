package pl.elpassion.space.pacman

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
    }

    interface PositionService {
        fun start(): Observable<IndoorwayPosition>
        fun stop()
    }
}