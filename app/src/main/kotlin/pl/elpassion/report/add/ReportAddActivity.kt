package pl.elpassion.report.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.add.details.ReportAddDetails
import pl.elpassion.report.add.details.ReportAddDetailsPaidVacationsFragment
import pl.elpassion.report.add.details.ReportAddDetailsRegularFragment
import pl.elpassion.report.datechooser.showDateDialog

class ReportAddActivity : AppCompatActivity(),
        ReportAdd.View,
        ReportAddDetails.Sender.Regular,
        ReportAddDetails.Sender.PaidVacations {

    private val controller by lazy {
        ReportAddController(intent.getStringExtra(ADD_DATE_KEY), this, ReportAdd.ApiProvider.get())
    }

    val items = listOf(ReportAddDetailsRegularFragment(), ReportAddDetailsPaidVacationsFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportAddAdd.setOnClickListener {
            controller.onReportAdd(getCurrentReportController())
        }
        reportAddDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        bottomNavigation.setOnNavigationItemSelectedListener { controller.onReportTypeChanged(it.itemId.toReportType()); true }
        reportAddReportDetailsForm.adapter = ReportAddPagerAdapter(items, this)
    }

    private fun Int.toReportType() = when (this) {
        R.id.action_regular_report -> ReportType.REGULAR
        R.id.action_sick_leave_report -> ReportType.SICK_LEAVE
        R.id.action_paid_vacations_report -> ReportType.PAID_VACATIONS
        R.id.action_unpaid_vacations_report -> ReportType.UNPAID_VACATIONS
        else -> throw IllegalArgumentException()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun showDate(date: String) {
        reportAddDate.text = date
    }

    override fun enableAddReportButton() {
        reportAddAdd.isEnabled = true
    }

    override fun showLoader() = showLoader(reportAddCoordinator)

    override fun hideLoader() = hideLoader(reportAddCoordinator)

    override fun onOptionsItemSelected(item: MenuItem) = handleClickOnBackArrowItem(item)

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportAddCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showRegularReportDetails() {
        reportAddReportDetailsForm.currentItem = 0
    }

    override fun showPaidVacationsReportDetails() {
    }

    override fun showSickLeaveReportDetails() {
        reportAddReportDetailsForm.currentItem = 1
    }

    override fun showUnpaidVacationsReportDetails() {
    }

    private fun getCurrentReportController(): ReportAddDetails.Controller {
        return items[reportAddReportDetailsForm.currentItem].controller!!
    }

    override fun sendAddReport(description: String, hours: String) {
        controller.sendAddReport(description, hours)
    }

    override fun sendAddReport(hours: String) {
        controller.sendAddReport(hours)
    }

    companion object {
        private val ADD_DATE_KEY = "dateKey"

        fun startForResult(activity: Activity, date: String, requestCode: Int) {
            activity.startActivityForResult(intent(activity, date), requestCode)
        }

        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(intent(activity), requestCode)
        }

        fun intent(context: Context) = Intent(context, ReportAddActivity::class.java)

        fun intent(context: Context, date: String) = intent(context).apply { putExtra(ADD_DATE_KEY, date) }
    }
}