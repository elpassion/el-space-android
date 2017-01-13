package pl.elpassion.space.pacman

import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.space.SubscriptionSubjectVerifier
import pl.elpassion.space.pacman.model.LocationUpdateEvent
import pl.elpassion.space.pacman.model.MapObject
import pl.elpassion.space.pacman.model.Position
import rx.Completable
import rx.subjects.PublishSubject


class PacManControllerTest {

    private val view = mock<PacMan.View>()
    private val playersSubject = PublishSubject.create<List<MapObject>>()
    private val positionSubject = SubscriptionSubjectVerifier<IndoorwayPosition>()
    private val mapView = mock<PacMan.MapView>()
    val positionService = mock<PacMan.PositionService>().apply {
        whenever(this.start()).thenReturn(positionSubject.observable)
    }
    val playersService = mock<PacMan.PlayersService>().apply {
        whenever(this.getPlayers()).thenReturn(playersSubject)
    }
    val pacManController = PacManController(view, mapView, positionService, playersService)

    @Test
    fun shouldShowMapLoadingErrorWhenLoadingMapFailed() {
        stubMapLoadingError()
        pacManController.onCreate()
        verify(view).showMapLoadingError()
    }

    @Test
    fun shouldInitializeMapOnLoadingSuccess() {
        stubMapLoadingSuccessfully()
        pacManController.onCreate()
        verify(mapView).initTextures()
    }

    @Test
    fun shouldNotShowErrorWhenMapLoadsCorrectly() {
        stubMapLoadingSuccessfully()
        pacManController.onCreate()
        verify(view, never()).showMapLoadingError()
    }

    @Test
    fun shouldNotInitializeTexturesOnLadingError() {
        stubMapLoadingError()
        pacManController.onCreate()
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
        val players = listOf(MapObject(id = "player1", position = Position(53.1, 54.2), ghost = MapObject.Type.GHOST))
        stubMapLoadingSuccessfully()
        pacManController.onCreate()
        playersSubject.onNext(players)
        verify(view).addMapObjects(players)
    }

    @Test
    fun shouldUpdatePlayersTwoTimes() {
        val player1 = MapObject(id = "player1", position = Position(53.1, 54.2), ghost = MapObject.Type.GHOST)
        val player2 = MapObject(id = "player2", position = Position(53.1, 54.2), ghost = MapObject.Type.GHOST)
        val players1 = listOf(player1, player2)
        val players2 = listOf(player1)
        stubMapLoadingSuccessfully()
        pacManController.onCreate()
        playersSubject.onNext(players1, players2)
        verify(view, times(2)).removeMapObjects(emptyList())
        verify(view).addMapObjects(emptyList())
        verify(view).addMapObjects(players1)
        verify(view).removeMapObjects(listOf(player2))
        verify(view).addMapObjects(players2)
    }

    @Test
    fun shouldShowPlayersUpdateErrorWhenUpdateFails() {
        stubMapLoadingSuccessfully()
        pacManController.onCreate()
        playersSubject.onError(RuntimeException())
        verify(view).showPlayersUpdateError()
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

    private fun stubMapLoadingSuccessfully() {
        whenever(mapView.loadMap()).thenReturn(Completable.complete())
    }

    private fun stubMapLoadingError() {
        whenever(mapView.loadMap()).thenReturn(Completable.error(RuntimeException()))
    }
}