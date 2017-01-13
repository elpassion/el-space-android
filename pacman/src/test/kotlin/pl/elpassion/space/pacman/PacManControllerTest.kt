package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.space.SubscriptionSubjectVerifier
import rx.subjects.PublishSubject


class PacManControllerTest {

    private val view = mock<PacMan.View>()
    private val loadMapSubject = PublishSubject.create<Unit>()
    private val playersSubject = PublishSubject.create<List<Player>>()
    private val positionSubject = SubscriptionSubjectVerifier<IndoorwayPosition>()
    private val mapView = mock<PacMan.MapView>().apply {
        whenever(this.loadMap()).thenReturn(loadMapSubject)
    }
    val positionService = mock<PacMan.PositionService>().apply {
        whenever(this.start()).thenReturn(positionSubject.observable)
    }
    val playersService = mock<PacMan.PlayersService>().apply {
        whenever(this.getPlayers()).thenReturn(playersSubject)
    }
    val panManController = PanManController(view, mapView, positionService, playersService)

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
    fun shouldStopPositionServiceOnPause() {
        panManController.onResume()
        panManController.onPause()
        positionSubject.assertUnsubscribe()
    }

    @Test
    fun shouldUpdatePositionOnReceived() {
        panManController.onResume()
        positionSubject.onNext(mock())
        verify(view).updatePosition(any())
    }

    @Test
    fun shouldUpdatePlayersOnReceived() {
        val players = listOf(Player(id = "player1", position = Position(53.1, 54.2)))
        panManController.onResume()
        playersSubject.onNext(players)
        verify(view).updatePlayers(players)
    }

    @Test
    fun shouldHandleMissingPermissionException() {
        panManController.onResume()
        positionSubject.onError(MissingPermissionException("permission"))
        verify(view).handleMissingPermissionException("permission")
    }

    @Test
    fun shouldHandleBLENotSupportedException() {
        panManController.onResume()
        positionSubject.onError(BLENotSupportedException())
        verify(view).handleBLENotSupportedException()
    }

    @Test
    fun shouldHandleBluetoothDisabledException() {
        panManController.onResume()
        positionSubject.onError(BluetoothDisabledException())
        verify(view).handleBluetoothDisabledException()
    }

    @Test
    fun shouldHandleLocationDisabledException() {
        panManController.onResume()
        positionSubject.onError(LocationDisabledException())
        verify(view).handleLocationDisabledException()
    }
}