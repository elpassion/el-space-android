package pl.elpassion.space.pacman

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.indoorway.android.location.sdk.service.PositioningServiceConnection
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import kotlinx.android.synthetic.main.pac_man_activity.*

class PacMan : AppCompatActivity() {

    val REQUEST_PERMISSION_CODE = 1
    var currentPosition: IndoorwayPosition? = null
    var serviceConnection: PositioningServiceConnection? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pac_man_activity)
        mapView.apply {
            setOnMapLoadCompletedListener<IndoorwayMapView> {

            }
            setOnMapLoadFailedListener<IndoorwayMapView> {

            }
            loadMap(buildingUuid, mapUuid)
        }
    }

    override fun onResume() {
        super.onResume()
        serviceConnection = IndoorwayLocationSdk.getInstance().positioningServiceConnection
        startPositioningService()
    }

    override fun onPause() {
        serviceConnection?.stop(this)
        super.onPause()
    }

    private fun startPositioningService() {
        try {
            serviceConnection?.apply {
                setOnPositionChangedListener<PositioningServiceConnection> { position ->
                    currentPosition = position
                    mapView.positionControl.setPosition(position, false)
                }
                setOnHeadingChangedListener<PositioningServiceConnection> { angle ->
                    mapView.positionControl.setHeading(angle)
                }
                start(this@PacMan)
            }
        } catch (e: MissingPermissionException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(e.permission), REQUEST_PERMISSION_CODE)
            }
        } catch (e: BLENotSupportedException) {
            showDialog(
                    title = "Sorry",
                    message = "Bluetooth Low Energy is not supported on your device. We are unable to find your indoor location.")
        } catch (e: BluetoothDisabledException) {
            showDialog(
                    title = "Please enable bluetooth",
                    message = "In order to find your indoor position, you need to enable bluetooth on your device.",
                    onPositiveButtonClick = {
                        startActivity(Intent().apply { action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS })
                    },
                    onNegativeButtonClick = {
                        closeDialog()
                    })
        } catch (e: LocationDisabledException) {
            showDialog(
                    title = "Please enable location",
                    message = "In order of finding your indoor position you must enable location in settings.",
                    onPositiveButtonClick = {
                        startActivity(Intent().apply { action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS })
                    },
                    onNegativeButtonClick = {
                        closeDialog()
                    })
        }
    }

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
}
