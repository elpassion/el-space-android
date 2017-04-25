package pl.elpassion.elspace.launcher

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.launcher_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.debate.login.DebateLoginActivity

class LauncherActivity : AppCompatActivity(), Launcher.View {

    private val controller by lazy {
        LauncherController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)
        launcherDebate.setOnClickListener { controller.onDebate() }
    }

    override fun openDebateLoginScreen() {
        DebateLoginActivity.start(this)
    }

    override fun openHubLoginScreen() = Unit
}