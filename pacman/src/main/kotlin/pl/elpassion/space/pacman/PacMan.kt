package pl.elpassion.space.pacman

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.indoorway.android.map.sdk.view.IndoorwayMapView


class PacMan : AppCompatActivity() {

    val indoorwayMapView by lazy { findViewById(R.id.mapView) as IndoorwayMapView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pac_man_activity)
        indoorwayMapView
                .setOnMapLoadCompletedListener<IndoorwayMapView> {

                }
                .setOnMapLoadFailedListener<IndoorwayMapView> {

                }
                .loadMap("hk64fd7-c6k", "5LEUvY1VATo")
    }
}
