package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.space.SubscriptionSubjectVerifier
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.model.Position
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
    val pacManController = PanManController(view, mapView, positionService, playersService)

    @Test
    fun shouldShowMapLoadingErrorWhenLoadingMapFailed() {
        pacManController.onCreate()
        loadMapSubject.onError(RuntimeException())
        verify(view).showMapLoadingError()
    }

    @Test
    fun shouldInitializeMapOnLoadingSuccess() {
        pacManController.onCreate()
        loadMapSubject.onNext(Unit)
        verify(mapView).initTextures()
    }

    @Test
    fun shouldNotShowErrorWhenMapLoadsCorrectly() {
        pacManController.onCreate()
        loadMapSubject.onNext(Unit)
        verify(view, never()).showMapLoadingError()
    }

    @Test
    fun shouldNotInitializeTexturesOnLadingError() {
        pacManController.onCreate()
        loadMapSubject.onError(RuntimeException())
        verify(mapView, never()).initTextures()
    }

    @Test
    fun shouldStopPositionServiceOnPause() {
        pacManController.onResume()
        pacManController.onPause()
        positionSubject.assertUnsubscribe()
    }

    @Test
    fun shouldUpdatePositionOnReceived() {
        pacManController.onResume()
        positionSubject.onNext(mock())
        verify(view).updatePosition(any())
    }

    @Test
    fun shouldUpdatePositionOnReceivedOnMap() {
        pacManController.onResume()
        positionSubject.onNext(mock())
        verify(mapView).updatePosition(any())
    }

    @Test
    fun shouldUpdatePlayersAfterMapInit() {
        val players = listOf(Player(id = "player1", position = Position(53.1, 54.2)))
        pacManController.onCreate()
        loadMapSubject.onNext(Unit)
        playersSubject.onNext(players)
        verify(view).updatePlayers(players)
    }

    @Test
    fun shouldUpdatePlayersTwoTimes() {
        val players1 = listOf(Player(id = "player1", position = Position(53.1, 54.2)))
        pacManController.onCreate()
        loadMapSubject.onNext(Unit)
        playersSubject.onNext(players1)
        val players2 = listOf(Player(id = "player1", position = Position(63.1, 64.2)))
        playersSubject.onNext(players2)
        verify(view).updatePlayers(players1)
        verify(view).updatePlayers(players2)
    }

    @Test
    fun shouldHandleMissingPermissionException() {
        pacManController.onResume()
        positionSubject.onError(MissingPermissionException("permission"))
        verify(view).handleMissingPermissionException("permission")
    }

    @Test
    fun shouldHandleBLENotSupportedException() {
        pacManController.onResume()
        positionSubject.onError(BLENotSupportedException())
        verify(view).handleBLENotSupportedException()
    }

    @Test
    fun shouldHandleBluetoothDisabledException() {
        pacManController.onResume()
        positionSubject.onError(BluetoothDisabledException())
        verify(view).handleBluetoothDisabledException()
    }

    @Test
    fun shouldHandleLocationDisabledException() {
        pacManController.onResume()
        positionSubject.onError(LocationDisabledException())
        verify(view).handleLocationDisabledException()
    }
}