package pl.elpassion

import android.app.Application
import pl.elpassion.common.SchedulersTransformerProvider
import pl.elpassion.project.common.ContextProvider

class MyFirstApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }
        SchedulersTransformerProvider.override = { SchedulersTransformerProvider.AndroidSchedulersTransformer }
    }
}