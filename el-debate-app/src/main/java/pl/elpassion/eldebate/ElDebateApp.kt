package pl.elpassion.eldebate

import android.app.Application
import pl.elpassion.elspace.common.ContextProvider

class ElDebateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }
    }
}