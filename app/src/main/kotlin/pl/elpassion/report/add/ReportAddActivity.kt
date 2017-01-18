package pl.elpassion.report.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.datechooser.showDateDialog
import rx.Observable

class ReportAddActivity : AppCompatActivity(), ReportAdd.View {

    private val controller by lazy {
        ReportAddController(intent.getStringExtra(ADD_DATE_KEY), this, ReportAdd.ApiProvider.get())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportAddDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        bottomNavigation.setOnNavigationItemSelectedListener { controller.onReportTypeChanged(it.itemId.toReportType()); true }
    }

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

    override fun showSickLeaveReportDetails() {
    }

    override fun showUnpaidVacationsReportDetails() {
    }

    override fun addReportClicks(): Observable<Unit> {
        return Observable.just(Unit)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_report_top_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
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