package pl.elpassion.elspace.hub.login

import android.support.v4.app.FragmentActivity
import android.view.View
import pl.elpassion.elspace.common.Provider

interface GoogleSingInController {
    fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View
}

object GoogleSingInControllerProvider : Provider<GoogleSingInController>({
    object : GoogleSingInController {
        override fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View {
            return View(activity)
        }
    }
})