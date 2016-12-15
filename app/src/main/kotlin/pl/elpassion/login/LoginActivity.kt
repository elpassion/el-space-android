package pl.elpassion.login

import android.annotation.TargetApi
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.R
import pl.elpassion.report.add.ReportAddActivity
import pl.elpassion.report.list.ReportListActivity


class LoginActivity : AppCompatActivity(), Login.View {

    private val controller = LoginController(this, LoginRepositoryProvider.get())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginButton.setOnClickListener { controller.onLogin(tokenInput.text.toString()) }
        controller.onCreate()
    }

    override fun hasHandlingShortcuts() = Build.VERSION.SDK_INT >= N_MR1

    @TargetApi(25)
    override fun creteAppShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        val addReportShortcut = createHubReportShortcut()
        shortcutManager.dynamicShortcuts = listOf(addReportShortcut)
    }

    @TargetApi(25)
    private fun createHubReportShortcut() = ShortcutInfo.Builder(this, "id_hub")
            .setShortLabel(getString(R.string.shortcut_report_hub_short))
            .setLongLabel(getString(R.string.shortcut_report_hub_long))
            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(ReportAddActivity.intent(this, ""))
            .build()

    override fun showEmptyLoginError() {
        emptyTokenError.visibility = VISIBLE
    }

    override fun openReportListScreen() {
        ReportListActivity.start(this)
        finish()
    }

}