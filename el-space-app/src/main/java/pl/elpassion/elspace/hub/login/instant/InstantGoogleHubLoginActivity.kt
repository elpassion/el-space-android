package pl.elpassion.elspace.hub.login.instant

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class InstantGoogleHubLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openOnLoggedInScreen(this)
    }

    companion object {
        lateinit var provideRepository: () -> InstantGoogleHubLogin.Repository
        lateinit var openOnLoggedInScreen: (Context) -> Unit
    }
}