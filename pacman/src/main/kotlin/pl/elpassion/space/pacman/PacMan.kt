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

class PacMan : AppCompatActivity() {

    val REQUEST_PERMISSION_CODE = 1
    val indoorwayMapView by lazy { findViewById(R.id.mapView) as IndoorwayMapView }
    var currentPosition: IndoorwayPosition? = null
    var serviceConnection: PositioningServiceConnection? = null
    var lastDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pac_man_activity)
        indoorwayMapView
                .setOnMapLoadCompletedListener<IndoorwayMapView> {

                }
                .setOnMapLoadFailedListener<IndoorwayMapView> {

                }
                .loadMap(buildingUuid, mapUuid)
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
                    indoorwayMapView.positionControl.setPosition(position, false)
                }
                setOnHeadingChangedListener<PositioningServiceConnection> { angle ->
                    indoorwayMapView.positionControl.setHeading(angle)
                }
                start(this@PacMan)
            }
        } catch (e: MissingPermissionException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(e.permission)
                requestPermissions(permissions, REQUEST_PERMISSION_CODE)
            }
        } catch (e: BLENotSupportedException) {
            closeDialog()
            lastDialog = AlertDialog.Builder(this)
                    .setTitle("Sorry")
                    .setMessage("Bluetooth Low Energy is not supported on your device. We are unable to find your indoor location.")
                    .setCancelable(true)
                    .show()
        } catch (e: BluetoothDisabledException) {
            closeDialog()
            lastDialog = AlertDialog.Builder(this)
                    .setTitle("Please enable bluetooth")
                    .setMessage("In order to find your indoor position, you need to enable bluetooth on your device.")
                    .setCancelable(true)
                    .setPositiveButton("OK", { dialog, which ->
                        val settingsIntent = Intent()
                        settingsIntent.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
                        startActivity(settingsIntent)
                    })
                    .setNegativeButton("Cancel", { dialog, which -> closeDialog() })
                    .show()
        } catch (e: LocationDisabledException) {
            closeDialog()
            lastDialog = AlertDialog.Builder(this)
                    .setTitle("Please enable location")
                    .setMessage("In order of finding your indoor position you must enable location in settings.")
                    .setCancelable(true)
                    .setPositiveButton("OK", { dialog, which ->
                        val settingsIntent = Intent()
                        settingsIntent.action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        startActivity(settingsIntent)
                    })
                    .setNegativeButton("Cancel", { dialog, which -> closeDialog() })
                    .show()
        }
    }

    private fun closeDialog() {
        if (lastDialog != null) {
            lastDialog?.dismiss()
        }
        lastDialog = null
    }
}
