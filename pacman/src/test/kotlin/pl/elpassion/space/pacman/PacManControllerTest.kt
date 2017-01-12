package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import rx.subjects.PublishSubject


class PacManControllerTest {

    private val view = mock<PacMan.View>()
    private val loadMapSubject = PublishSubject.create<Unit>()
    private val mapView = mock<PacMan.MapView>().apply {
        whenever(this.loadMap()).thenReturn(loadMapSubject)
    }
    val positionService = mock<PacMan.PositionService>()
    val panManController = PanManController(view, mapView, positionService)

    @Test
    fun shouldShowMapLoadingErrorWhenLoadingMapFailed() {
        panManController.onCreate()
        loadMapSubject.onError(RuntimeException())
        verify(view).showMapLoadingError()
    }

    @Test
    fun shouldInitializeMapOnLoadingSuccess() {
        panManController.onCreate()
        loadMapSubject.onNext(Unit)
        verify(mapView).initTextures()
    }

    @Test
    fun shouldNotShowErrorWhenMapLoadsCorrectly() {
        panManController.onCreate()
        loadMapSubject.onNext(Unit)
        verify(view, never()).showMapLoadingError()
    }

    @Test
    fun shouldNotInitializeTexturesOnLadingError() {
        panManController.onCreate()
        loadMapSubject.onError(RuntimeException())
        verify(mapView, never()).initTextures()
    }

    @Test
    fun shouldStartLocationListenerOnResume() {
        panManController.onResume()
        verify(positionService).start()
    }
}