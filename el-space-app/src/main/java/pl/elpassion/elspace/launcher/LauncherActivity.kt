package pl.elpassion.elspace.launcher

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.launcher_activity.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.debate.login.DebateLoginActivity
import pl.elpassion.elspace.hub.login.instant.InstantGoogleHubLoginActivity

class LauncherActivity : AppCompatActivity(), Launcher.View {

    private val controller by lazy { LauncherController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)
        launcherDebateButton.setOnClickListener { controller.onDebate() }
        launcherHubButton.setOnClickListener { controller.onHub() }
    }

    override fun openDebateLoginScreen() = DebateLoginActivity.start(this)

    override fun openHubLoginScreen() = InstantGoogleHubLoginActivity.start(this)
}