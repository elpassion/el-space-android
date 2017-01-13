package pl.elpassion.space.pacman

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.gles.GLRendererSurfaceView
import kotlinx.android.synthetic.main.pac_man_activity.*
import pl.elpassion.space.pacman.api.PlayersServiceImpl
import pl.elpassion.space.pacman.api.WebSocketClientApiImpl
import pl.elpassion.space.pacman.api.WebSocketClientImpl
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.utils.CreaturePicker


class PacManActivity : AppCompatActivity(), PacMan.View {

    val REQUEST_PERMISSION_CODE = 1
    var currentPosition: IndoorwayPosition? = null
    var alertDialog: AlertDialog? = null
    val controller by lazy {
        PacManController(this, PacManMapView(mapView), PacManPositionService(this), PlayersServiceImpl(WebSocketClientImpl("ws://192.168.1.19:8181/ws", WebSocketClientApiImpl())))
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

    override fun showPlayersUpdateError() {
        Toast.makeText(this, "Players update error", Toast.LENGTH_SHORT).show()
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
        veryBadInvalidate()
        currentLocationView.text = position.coordinates.getText()
    }

    override fun addPlayers(players: List<Player>) {
        players.forEach {
            mapView.markerControl.add(CreaturePicker().createCreature(player))
        }
        veryBadInvalidate()
    }

    override fun removePlayers(players: List<Player>) {
        players.forEach {
            mapView.markerControl.remove(it.id)
        }
        veryBadInvalidate()
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
