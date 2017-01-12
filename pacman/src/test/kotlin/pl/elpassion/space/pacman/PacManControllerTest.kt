package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import rx.subjects.PublishSubject


class PacManControllerTest {

    private val view = mock<PacMan.View>()
    private val loadMapSubject = PublishSubject.create<Unit>()
    private val positionSubject = PublishSubject.create<IndoorwayPosition>()
    private val mapView = mock<PacMan.MapView>().apply {
        whenever(this.loadMap()).thenReturn(loadMapSubject)
    }
    val positionService = mock<PacMan.PositionService>().apply {
        whenever(this.start()).thenReturn(positionSubject)
    }
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

    @Test
    fun shouldStopPositionServiceOnPause() {
        panManController.onPause()
        verify(positionService).stop()
    }

    @Test
    fun shouldUpdatePositionOnReceived() {
        panManController.onResume()
        positionSubject.onNext(mock())
        verify(view).updatePosition(any())
    }
}