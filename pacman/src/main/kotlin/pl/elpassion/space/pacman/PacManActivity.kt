package pl.elpassion.space.pacman

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.gles.GLRendererSurfaceView
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.service.PositioningServiceConnection
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.textures.DrawableTexture
import kotlinx.android.synthetic.main.pac_man_activity.*
import rx.AsyncEmitter
import rx.Observable

val SPRITE_SHEET_ID = "sprite-sheet"
val pacMan = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(144, 64, 100, 128), Size(980f, 640f))

class PacManActivity : AppCompatActivity(), PacMan.View {

    val REQUEST_PERMISSION_CODE = 1
    var currentPosition: IndoorwayPosition? = null
    var alertDialog: AlertDialog? = null
    val controller by lazy {
        PanManController(this, PacManMapView(mapView), PacManPositionService(this), object : PacMan.PlayersService {
            override fun getPlayers() = TODO()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pac_man_activity)
        controller.onCreate()
        logLocationButton.setOnClickListener { logPosition() }
    }

    override fun showMapLoadingError() {
        showDialog("Map not loaded", "Error while loading map.")
    }

    override fun onResume() {
        super.onResume()
        controller.onResume()
    }

    override fun onPause() {
        controller.onPause()
        super.onPause()
    }

    override fun handleMissingPermissionException(permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(permission), REQUEST_PERMISSION_CODE)
        }
    }

    override fun handleBLENotSupportedException() {
        showDialog(
                title = "Sorry",
                message = "Bluetooth Low Energy is not supported on your device. We are unable to find your indoor location.")
    }

    override fun handleBluetoothDisabledException() {
        showDialog(
                title = "Please enable bluetooth",
                message = "In order to find your indoor position, you need to enable bluetooth on your device.",
                onPositiveButtonClick = {
                    startActivity(Intent().apply { action = Settings.ACTION_BLUETOOTH_SETTINGS })
                },
                onNegativeButtonClick = {
                    closeDialog()
                })
    }

    override fun handleLocationDisabledException() {
        showDialog(
                title = "Please enable location",
                message = "In order of finding your indoor position you must enable location in settings.",
                onPositiveButtonClick = {
                    startActivity(Intent().apply { action = Settings.ACTION_LOCATION_SOURCE_SETTINGS })
                },
                onNegativeButtonClick = {
                    closeDialog()
                })
    }

    private fun logPosition() {
        Log.i("PositionLogger", currentPosition?.coordinates?.getText() ?: "no position")
    }

    override fun updatePosition(position: IndoorwayPosition) {
        currentPosition = position
        mapView.markerControl.add(pacMan.asDrawable("pacman", position.coordinates, Size(2f, 2.34f)))
        veryBadInvalidate()
        currentLocationView.text = position.coordinates.getText()
    }

    override fun updatePlayers(players: List<Player>) {

    }

    private fun Coordinates.getText() = "lat: $latitude, long: $longitude"

    private fun showDialog(title: String, message: String,
                           onPositiveButtonClick: (() -> Unit)? = null,
                           onNegativeButtonClick: (() -> Unit)? = null) {
        closeDialog()
        alertDialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .apply {
                    if (onPositiveButtonClick != null) {
                        setPositiveButton("OK", { dialog, which -> onPositiveButtonClick() })
                    }
                    if (onNegativeButtonClick != null) {
                        setNegativeButton("Cancel", { dialog, which -> onNegativeButtonClick() })
                    }
                }
                .show()
    }

    private fun closeDialog() {
        if (alertDialog != null) {
            alertDialog?.dismiss()
        }
        alertDialog = null
    }

    private fun veryBadInvalidate() {
        val view: GLRendererSurfaceView = mapView.getChildAt(0) as GLRendererSurfaceView
        view.requestRender()
    }

}

class PacManMapView(val mapView: IndoorwayMapView) : PacMan.MapView {
    override fun loadMap(): Observable<Unit> {
        return Observable.fromAsync<Unit>({ emitter ->
            mapView.apply {
                setOnMapLoadCompletedListener<IndoorwayMapView> {
                    emitter.onNext(Unit)
                }
                setOnMapLoadFailedListener<IndoorwayMapView> {
                    emitter.onError(RuntimeException())
                }
                loadMap(buildingUuid, mapUuid)
            }
        }, AsyncEmitter.BackpressureMode.BUFFER)
    }

    override fun initTextures() {
        mapView.markerControl.apply {
            // sprite sheet registration
            val bitmap = BitmapFactory.decodeResource(mapView.context.resources, R.drawable.sprites)
            registerTexture(DrawableTexture(SPRITE_SHEET_ID, bitmap))

            for ((idx, point) in TEST_POINTS.withIndex())
                add(DrawableCircle(idx.toString(), 0.2f, Color.argb(255, 255, 255 - idx, idx), Color.RED, 0.1f, point))
        }
    }
}

class PacManPositionService(val context: Context) : PacMan.PositionService {
    override fun start(): Observable<IndoorwayPosition> {
        return Observable.fromAsync({ emitter ->
            IndoorwayLocationSdk.getInstance().positioningServiceConnection.run {
                emitter.setCancellation {
                    stop(context)
                }
                setOnPositionChangedListener<PositioningServiceConnection> { position ->
                    emitter.onNext(position)
                }
                try {
                    start(context)
                } catch (exception: Exception) {
                    emitter.onError(exception)
                }
            }
        }, AsyncEmitter.BackpressureMode.BUFFER)
    }
}
