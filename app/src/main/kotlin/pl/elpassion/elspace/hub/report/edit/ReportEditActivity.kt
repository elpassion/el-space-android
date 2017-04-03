package pl.elpassion.elspace.hub.report.edit

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
import com.jakewharton.rxbinding.support.design.widget.itemSelections
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.choose.ProjectChooseActivity
import pl.elpassion.elspace.hub.report.*
import pl.elpassion.elspace.hub.report.datechooser.showDateDialog
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ReportEditActivity : AppCompatActivity(), ReportEdit.View {

    private val controller by lazy {
        ReportEditController(
                report = intent.getSerializableExtra(REPORT_KEY) as Report,
                view = this,
                api = ReportEdit.ApiProvider.get(),
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }
    private val toolbarClicks by lazy { toolbar.menuClicks() }

    private var selectedProject: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportEditDate.setOnClickListener {
            showDateDialog(supportFragmentManager) {
                controller.onDateChanged(it)
            }
        }
        reportEditProjectName.setOnClickListener {
            ProjectChooseActivity.startForResult(this, CHOOSE_PROJECT_REQUEST_CODE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_PROJECT_REQUEST_CODE && data != null) {
            controller.onProjectChanged(ProjectChooseActivity.getProject(data))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_report_top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return handleClickOnBackArrowItem(item)
    }

    override fun showReportType(type: ReportType) {
        bottomNavigation.menu.findItem(type.toActionId()).isChecked = true
    }

    override fun showDate(date: String) {
        reportEditDate.setText(date)
    }

    override fun showReportedHours(reportedHours: Double) {
        reportEditHours.setText(reportedHours.toStringWithoutZeroes())
    }

    override fun showProject(project: Project) {
        selectedProject = project
        reportEditProjectName.setText(project.name)
    }

    override fun showDescription(description: String) {
        reportEditDescription.setText(description)
    }

    override fun reportTypeChanges(): Observable<ReportType> = bottomNavigation.itemSelections().map { it.itemId.toReportType() }

    override fun showRegularForm() {
        showHourlyForm()
        reportEditProjectNameLayout.show()
        reportEditDescriptionLayout.show()
    }

    override fun showPaidVacationsForm() {
        showHourlyForm()
        reportEditProjectNameLayout.hide()
        reportEditDescriptionLayout.hide()
    }

    override fun showSickLeaveForm() {
        reportEditAdditionalInfo.setText(R.string.report_add_sick_leave_info)
        showDailyForm()
    }

    override fun showUnpaidVacationsForm() {
        reportEditAdditionalInfo.setText(R.string.report_add_unpaid_vacations_info)
        showDailyForm()
    }

    override fun editReportClicks(): Observable<ReportViewModel> =
            toolbarClicks.onMenuItemAction(R.id.editReport).map {
                getReportViewModel(bottomNavigation.menu.checkedItemId)
            }

    override fun removeReportClicks(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.removeReport)

    private fun getReportViewModel(checkedMenuItem: Int): ReportViewModel {
        val date = reportEditDate.text.toString()
        val hours = reportEditHours.text.toString()
        val description = reportEditDescription.text.toString()
        return when (checkedMenuItem) {
            R.id.action_regular_report -> RegularViewModel(date, selectedProject, description, hours)
            R.id.action_paid_vacations_report -> PaidVacationsViewModel(date, hours)
            R.id.action_unpaid_vacations_report, R.id.action_sick_leave_report -> DailyViewModel(date)
            else -> throw IllegalArgumentException()
        }
    }

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showLoader() = showLoader(reportEditCoordinator)

    override fun hideLoader() = hideLoader(reportEditCoordinator)

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportEditCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showEmptyProjectError() {
        Snackbar.make(reportEditCoordinator, R.string.empty_project_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showEmptyDescriptionError() {
        Snackbar.make(reportEditCoordinator, R.string.empty_description_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    private fun showHourlyForm() {
        reportEditDateLayout.show()
        reportEditHoursLayout.show()
        reportEditAdditionalInfo.hide()
    }

    private fun showDailyForm() {
        reportEditDateLayout.show()
        reportEditHoursLayout.hide()
        reportEditProjectNameLayout.hide()
        reportEditDescriptionLayout.hide()
        reportEditAdditionalInfo.show()
    }

    companion object {

        private val REPORT_KEY = "report_key"
        private val CHOOSE_PROJECT_REQUEST_CODE = 789

        fun startForResult(activity: Activity, requestCode: Int, report: Report) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }

        fun intent(context: Context, report: Report) = intent(context).apply { putExtra(REPORT_KEY, report) }

        private fun intent(context: Context) = Intent(context, ReportEditActivity::class.java)
    }
}