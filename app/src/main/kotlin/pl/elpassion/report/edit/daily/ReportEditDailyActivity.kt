package pl.elpassion.report.edit.daily

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.elpassion.R
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.report.DailyReport

class ReportEditDailyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daily_report_edit_activity)
        showBackArrowOnActionBar()
    }

    companion object {
        private val REPORT_KEY = "report_key"

        fun intent(context: Context, report: DailyReport) = Intent(context, ReportEditDailyActivity::class.java).apply {
            putExtra(REPORT_KEY, report)
        }

        fun startForResult(activity: Activity, report: DailyReport, requestCode: Int) {
            activity.startActivityForResult(ReportEditDailyActivity.intent(activity, report), requestCode)
        }
    }
}