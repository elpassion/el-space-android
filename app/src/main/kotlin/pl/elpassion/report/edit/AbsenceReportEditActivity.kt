package pl.elpassion.report.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.paid_vacation_report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.DailyReport
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.Report
import pl.elpassion.report.datechooser.showDateDialog
import pl.elpassion.report.edit.service.ReportEditServiceImpl

class AbsenceReportEditActivity : AppCompatActivity(), ReportEdit.PaidVacation.View {

    private val report by lazy { intent.getSerializableExtra(REPORT_KEY) as Report }
    private val hours by lazy { intent.getDoubleExtra(REPORT_HOURS_KEY, 0.00) }

    private val controller by lazy {
        AbsenceReportEditController(this, ReportEditServiceImpl(ReportEdit.EditApiProvider.get()), ReportEdit.RemoveApiProvider.get())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.paid_vacation_report_edit_activity)
        controller.onCreate(report)
        showBackArrowOnActionBar()
        reportEditDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        reportEditHours.setOnTouchListener { view, motionEvent -> reportEditHours.text = null;false }
        reportEditSaveButton.setOnClickListener { controller.onSaveReport(reportEditHours.text.toString()) }
    }

    override fun hideReportHours() {
        reportEditHours.hide()
    }

    override fun showReportHours(reportHours: Double) {
        reportEditHours.show()
        reportEditHours.setText("$reportHours")
    }

    override fun showDate(date: String) {
        reportEditDate.text = getPerformedAtString(report.year, report.month, report.day)
    }

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportEditCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun hideLoader() {
        hideLoader(reportEditCoordinator)
    }

    override fun showLoader() {
        showLoader(reportEditCoordinator)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_report_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_remove_report) {
            controller.onRemoveReport()
            return true
        } else {
            return handleClickOnBackArrowItem(item)
        }
    }

    companion object {
        private val REPORT_KEY = "report_key"
        private val REPORT_HOURS_KEY = "report_hours_key"

        fun intent(context: Context, report: PaidVacationHourlyReport) = Intent(context, AbsenceReportEditActivity::class.java).apply {
            putExtra(REPORT_KEY, report)
            putExtra(REPORT_HOURS_KEY, report.reportedHours)
        }

        fun intent(context: Context, report: DailyReport) = Intent(context, AbsenceReportEditActivity::class.java).apply {
            putExtra(REPORT_KEY, report)
        }

        fun startForResult(activity: Activity, report: PaidVacationHourlyReport, requestCode: Int) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }

        fun startForResult(activity: Activity, report: DailyReport, requestCode: Int) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }
    }
}