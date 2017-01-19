package pl.elpassion.report.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding.support.design.widget.itemSelections
import com.jakewharton.rxbinding.support.v7.widget.itemClicks
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepositoryProvider
import pl.elpassion.report.add.details.ReportAddDetails
import pl.elpassion.report.datechooser.showDateDialog
import rx.Observable

class ReportAddActivity : AppCompatActivity(), ReportAdd.View, ReportAddDetails.View.Regular {

    private val controller by lazy {
        ReportAddController(intent.getStringExtra(ADD_DATE_KEY), this, ReportAdd.ApiProvider.get(), LastSelectedProjectRepositoryProvider.get())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportAddDate.setOnClickListener { showDateDialog(supportFragmentManager, {}) }
    }

    override fun reportTypeChanges(): Observable<ReportType> = bottomNavigation.itemSelections().map { it.itemId.toReportType() }

    private fun Int.toReportType() = when (this) {
        R.id.action_regular_report -> ReportType.REGULAR
        R.id.action_paid_vacations_report -> ReportType.PAID_VACATIONS
        R.id.action_sick_leave_report -> ReportType.SICK_LEAVE
        R.id.action_unpaid_vacations_report -> ReportType.UNPAID_VACATIONS
        else -> throw IllegalArgumentException()
    }

    override fun showDate(date: String) {
        reportAddDate.setText(date)
    }

    override fun showLoader() = showLoader(reportAddCoordinator)

    override fun hideLoader() = hideLoader(reportAddCoordinator)

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportAddCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun addReportClicks(): Observable<ReportViewModel> {
        return toolbar.itemClicks().map {
            val selectedDate = reportAddDate.text.toString()
            val checkMenuItem = bottomNavigation.menu.items.first { it.isChecked }.itemId
            when (checkMenuItem) {
                R.id.action_regular_report -> RegularReport(selectedDate, null, reportAddDescription.text.toString(), reportAddHours.text.toString())
                R.id.action_paid_vacations_report -> PaidVacationsReport(selectedDate, reportAddHours.text.toString())
                R.id.action_unpaid_vacations_report -> UnpaidVacationsReport(selectedDate)
                R.id.action_sick_leave_report -> SickLeaveReport(selectedDate)
                else -> throw IllegalArgumentException(checkMenuItem.toString())
            }
        }
    }

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showHoursInput() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProjectChooser() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showDescriptionInput() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideDescriptionInput() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProjectChooser() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideHoursInput() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_report_top_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun showSelectedProject(project: Project) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openProjectChooser() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEmptyDescriptionError() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEmptyProjectError() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun projectClickEvents(): Observable<Unit> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun projectChanges(): Observable<Project> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
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

private val Menu.items: List<MenuItem>
    get() = (0 until size()).map { getItem(it) }