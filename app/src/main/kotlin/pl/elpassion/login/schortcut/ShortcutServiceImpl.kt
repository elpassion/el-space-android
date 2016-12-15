package pl.elpassion.login.schortcut

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import pl.elpassion.R
import pl.elpassion.report.add.ReportAddActivity

class ShortcutServiceImpl(val context: Context) : ShortcutService {

    override fun hasHandlingShortcuts() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

    @TargetApi(25)
    override fun creteAppShortcuts() {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        val addReportShortcut = createHubReportShortcut()
        shortcutManager.dynamicShortcuts = listOf(addReportShortcut)
    }

    @TargetApi(25)
    private fun createHubReportShortcut() = ShortcutInfo.Builder(context, "id_hub")
            .setShortLabel(context.getString(R.string.shortcut_report_hub_short))
            .setLongLabel(context.getString(R.string.shortcut_report_hub_long))
            .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
            .setIntent(ReportAddActivity.intent(context, ""))
            .build()

}