package pl.elpassion.eldebate

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class ElDebateApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }
        Fabric.with(this, Crashlytics())
        DebateLoginActivity.showBackArrow = false
    }
}