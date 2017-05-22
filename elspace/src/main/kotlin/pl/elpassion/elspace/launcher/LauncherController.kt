package pl.elpassion.elspace.launcher

class LauncherController(private val view: Launcher.View) {

    fun onDebate() {
        view.openDebateLoginScreen()
    }

    fun onHub() {
        view.openHubLoginScreen()
    }
}