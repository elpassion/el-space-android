package pl.elpassion.eldebate

import android.support.multidex.MultiDexApplication
import android.util.Log
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class ElDebateApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }
        Fabric.with(this, Crashlytics())
        DebateLoginActivity.showBackArrow = false
        setRxJavaErrorHandler()
    }

    private fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException) {
                Log.e("UndeliverableException", "RxJava threw UndeliverableException")
            } else throw throwable
        }
    }
}