package pl.elpassion.space.pacman

import android.app.Application
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.IndoorwayMapSdk


class PacManApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val trafficApiKey = "85e4c1ca-3af6-4d37-92b3-13a80ca5ceed"
        IndoorwayMapSdk.init(this, trafficApiKey)
        IndoorwayLocationSdk.init(this, trafficApiKey)
    }
}