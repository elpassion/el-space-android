package pl.elpassion.report.edit.daily

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.daily_report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.report.DailyReport
import pl.elpassion.report.edit.ReportEdit
import pl.elpassion.report.edit.service.ReportEditServiceImpl

class ReportEditDailyActivity : AppCompatActivity(), ReportEdit.Daily.View {

    private val report by lazy { intent.getSerializableExtra(REPORT_KEY) as DailyReport }

    private val controller by lazy {
        ReportEditDailyController(this, ReportEditServiceImpl(ReportEdit.EditApiProvider.get()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daily_report_edit_activity)
        showBackArrowOnActionBar()
        controller.onCreate(report)
    }

    override fun showLoader() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoader() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(ex: Throwable) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showDate(date: String) {
        reportEditDate.text = report.date
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