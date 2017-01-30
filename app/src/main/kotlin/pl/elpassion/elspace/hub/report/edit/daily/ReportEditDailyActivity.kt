package pl.elpassion.elspace.hub.report.edit.daily

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import kotlinx.android.synthetic.main.daily_report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.datechooser.showDateDialog
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import pl.elpassion.elspace.hub.report.edit.service.ReportEditServiceImpl

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
        reportEditDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        reportEditSaveButton.setOnClickListener { controller.onSaveReport() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_report_menu, menu)
        return true
    }

    override fun showLoader() {

    }

    override fun hideLoader() {

    }

    override fun showError(ex: Throwable) {
        throw UnsupportedOperationException(ex) //To change body of created functions use File | Settings | File Templates.
    }

    override fun close() {

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