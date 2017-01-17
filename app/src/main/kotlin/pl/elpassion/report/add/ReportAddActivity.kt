package pl.elpassion.report.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding.support.v4.view.pageSelections
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.add.details.ReportAddDetails
import pl.elpassion.report.add.details.ReportAddDetailsFragment
import pl.elpassion.report.add.details.paidvacations.ReportAddDetailsPaidVacationsFragment
import pl.elpassion.report.add.details.regular.ReportAddDetailsRegularFragment
import pl.elpassion.report.add.details.sickleave.ReportAddDetailsSickLeaveFragment
import pl.elpassion.report.add.details.unpaidvacations.ReportAddDetailsUnpaidVacationsFragment
import pl.elpassion.report.datechooser.showDateDialog

class ReportAddActivity : AppCompatActivity(),
        ReportAdd.View,
        ReportAddDetails.Sender.Regular,
        ReportAddDetails.Sender.PaidVacations,
        ReportAddDetails.Sender.SickLeave,
        ReportAddDetails.Sender.UnpaidVacations {

    private val controller by lazy {
        ReportAddController(intent.getStringExtra(ADD_DATE_KEY), this, ReportAdd.ApiProvider.get())
    }

    private val items = listOf(ReportAddDetailsRegularFragment(),
            ReportAddDetailsPaidVacationsFragment(),
            ReportAddDetailsSickLeaveFragment(),
            ReportAddDetailsUnpaidVacationsFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportAddDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        bottomNavigation.setOnNavigationItemSelectedListener { controller.onReportTypeChanged(it.itemId.toReportType()); true }
        reportAddReportDetailsForm.adapter = ReportAddPagerAdapter(items, this)
        reportAddReportDetailsForm.pageSelections().subscribe(bottomNavigation.selectItemAtPosition())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_report_top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addReport -> controller.onReportAdd(getCurrentReportController())
        }
        return handleClickOnBackArrowItem(item)
    }

    private fun Int.toReportType() = when (this) {
        R.id.action_regular_report -> ReportType.REGULAR
        R.id.action_paid_vacations_report -> ReportType.PAID_VACATIONS
        R.id.action_sick_leave_report -> ReportType.SICK_LEAVE
        R.id.action_unpaid_vacations_report -> ReportType.UNPAID_VACATIONS
        else -> throw IllegalArgumentException()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun showDate(date: String) {
        reportAddDate.setText(date)
    }

    override fun showLoader() = showLoader(reportAddCoordinator)

    override fun hideLoader() = hideLoader(reportAddCoordinator)

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportAddCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showRegularReportDetails() {
        setDetailsFormFor { item -> item is ReportAddDetailsRegularFragment }
    }

    override fun showPaidVacationsReportDetails() {
        setDetailsFormFor { item -> item is ReportAddDetailsPaidVacationsFragment }
    }

    override fun showSickLeaveReportDetails() {
        setDetailsFormFor { item -> item is ReportAddDetailsSickLeaveFragment }
    }

    override fun showUnpaidVacationsReportDetails() {
        setDetailsFormFor { item -> item is ReportAddDetailsUnpaidVacationsFragment }
    }

    private fun setDetailsFormFor(predicate: (ReportAddDetailsFragment) -> Boolean) {
        reportAddReportDetailsForm.currentItem = items.indexOfFirst(predicate)
    }

    private fun getCurrentReportController(): ReportAddDetails.Controller {
        return items[reportAddReportDetailsForm.currentItem].controller!!
    }

    override fun addRegularReport(description: String, hours: String, projectId: Long) {
        controller.addRegularReport(description, hours, projectId)
    }

    override fun addPaidVacationsReport(hours: String) {
        controller.addPaidVacationsReport(hours)
    }

    override fun addSickLeaveReport() {
        controller.addSickLeaveReport()
    }

    override fun addUnpaidVacationsReport() {
        controller.addUnpaidVacationsReport()
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

private fun BottomNavigationView.selectItemAtPosition() = rx.functions.Action1 { position: Int ->
    menu.items[position].isChecked = true
}

private val Menu.items: List<MenuItem>
    get() = (0 until size()).map { getItem(it) }
