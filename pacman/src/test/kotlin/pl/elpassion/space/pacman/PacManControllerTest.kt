package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import rx.Observable
import rx.subjects.PublishSubject

class PacManControllerTest {

    val view = mock<PacMan.View>()
    val loadMapSubject = PublishSubject.create<Unit>()
    val mapView = mock<PacMan.MapView>().apply {
        whenever(this.loadMap()).thenReturn(loadMapSubject)
    }

    @Test
    fun shouldLoadMapOnCreate() {
        PanManController(view, mapView).onCreate()
        loadMapSubject.onNext(Unit)
        verify(mapView).loadMap()
    }

    @Test
    fun shouldShowMapLoadingErrorWhenLoadingMapFailed() {
        PanManController(view, mapView).onCreate()
        loadMapSubject.onError(RuntimeException())
        verify(view).showMapLoadingError()
    }

    @Test
    fun shouldInitializeMapOnLoadingSuccess() {
        PanManController(view, mapView).onCreate()
        loadMapSubject.onNext(Unit)
        verify(mapView).initTextures()
    }

    @Test
    fun shouldNotShowErrorWhenMapLoadsCorrectly() {
        PanManController(view, mapView).onCreate()
        loadMapSubject.onNext(Unit)
        verify(view, never()).showMapLoadingError()
    }
}

class PanManController(val view: PacMan.View, val mapView: PacMan.MapView) {
    fun onCreate() {
        mapView.loadMap().subscribe({ }, {
            view.showMapLoadingError()
        })
        mapView.initTextures()
    }
}

interface PacMan {
    interface MapView {
        fun loadMap(): Observable<Unit>
        fun initTextures()
    }

    interface View {
        fun showMapLoadingError()
    }
}
