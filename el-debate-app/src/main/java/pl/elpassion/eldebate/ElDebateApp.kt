package pl.elpassion.eldebate

import android.app.Application
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class ElDebateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }

        DebateLoginActivity.showBackArrow = false
    }
}