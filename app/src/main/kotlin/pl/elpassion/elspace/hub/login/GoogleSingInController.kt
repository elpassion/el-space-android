package pl.elpassion.elspace.hub.login

import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.view.View
import pl.elpassion.elspace.common.Provider

interface GoogleSingInController {
    fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

object GoogleSingInControllerProvider : Provider<GoogleSingInController>({
    object : GoogleSingInController {
        override fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View {
            return View(activity)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = Unit
    }
})