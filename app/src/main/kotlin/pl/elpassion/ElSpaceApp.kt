package pl.elpassion

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import pl.elpassion.common.ContextProvider
import pl.elpassion.common.SchedulersTransformerProvider

class ElSpaceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        ContextProvider.override = { applicationContext }
        SchedulersTransformerProvider.override = { SchedulersTransformerProvider.AndroidSchedulersTransformer }
    }
}