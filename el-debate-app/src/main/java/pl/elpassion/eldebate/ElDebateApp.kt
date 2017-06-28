package pl.elpassion.eldebate

import android.support.multidex.MultiDexApplication
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class ElDebateApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.override = { applicationContext }

        DebateLoginActivity.showBackArrow = false
    }
}