package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class PacManControllerTest {

    val view = mock<PacMan.View>()
    val mapView = mock<PacMan.MapView>()

    @Test
    fun shouldLoadMapOnCreate() {
        PanManController(view, mapView).onCreate()
        verify(mapView).loadMap()
    }

    @Test
    fun shouldShowMapLoadingErrorWhenLoadingMapFailed() {
        PanManController(view, mapView).onCreate()
        verify(view).showMapLoadingError()
    }

    @Test
    fun shouldInitializeMapOnLoadingSuccess() {
        PanManController(view, mapView).onCreate()
        verify(mapView).initTextures()
    }
}

class PanManController(val view: PacMan.View, val mapView: PacMan.MapView) {
    fun onCreate() {
        mapView.loadMap()
        view.showMapLoadingError()
        mapView.initTextures()
    }
}

interface PacMan {
    interface MapView {
        fun loadMap()
        fun initTextures()
    }

    interface View {
        fun showMapLoadingError()
    }
}
