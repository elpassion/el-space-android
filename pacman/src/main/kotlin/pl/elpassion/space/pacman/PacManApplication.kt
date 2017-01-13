package pl.elpassion.space.pacman

import android.app.Application
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.map.sdk.IndoorwayMapSdk
import pl.elpassion.space.pacman.config.trafficApiKey
import pl.elpassion.space.pacman.utils.CreaturePicker
import java.util.*


class PacManApplication : Application() {

    override fun onCreate() {
        CreaturePicker.pacManName = UUID.randomUUID().toString()
        super.onCreate()
        IndoorwayMapSdk.init(this, trafficApiKey)
        IndoorwayLocationSdk.init(this, trafficApiKey)
    }
}