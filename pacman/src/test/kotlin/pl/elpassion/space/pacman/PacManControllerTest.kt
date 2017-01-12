package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class PacManControllerTest {

    val mapView = mock<PacMan.MapView>()

    @Test
    fun shouldLoadMapOnCreate() {
        PanManController(mapView).onCreate()
        verify(mapView).loadMap()
    }
}

class PanManController(val mapView: PacMan.MapView) {
    fun onCreate() {
        mapView.loadMap()
    }
}

interface PacMan {
    interface MapView {
        fun loadMap()
    }
}
