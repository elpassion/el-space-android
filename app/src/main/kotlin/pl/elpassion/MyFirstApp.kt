package pl.elpassion

import android.app.Application
import pl.elpassion.common.ContextProvider
import pl.elpassion.common.SchedulersTransformerProvider

class MyFirstApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }
        SchedulersTransformerProvider.override = { SchedulersTransformerProvider.AndroidSchedulersTransformer }
    }
}