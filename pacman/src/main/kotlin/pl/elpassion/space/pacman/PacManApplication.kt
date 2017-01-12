package pl.elpassion.space.pacman

import android.app.Application
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.IndoorwayMapSdk


class PacManApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        IndoorwayMapSdk.init(this, trafficApiKey)
        IndoorwayLocationSdk.init(this, trafficApiKey)
    }
}