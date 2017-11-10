package pl.elpassion.elspace.hub.report.add

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
import com.jakewharton.rxbinding2.support.design.widget.itemSelections
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.checkedItemId
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.choose.ProjectChooseActivity
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepositoryProvider
import pl.elpassion.elspace.hub.report.ReportType
import pl.elpassion.elspace.hub.report.ReportViewModel
import pl.elpassion.elspace.hub.report.datechooser.showDateDialog
import pl.elpassion.elspace.hub.report.getReportViewModel
import pl.elpassion.elspace.hub.report.toReportType

class ReportAddActivity : AppCompatActivity(), ReportAdd.View {

    private val controller by lazy {
        ReportAddController(
                date = intent.getStringExtra(ADD_DATE_KEY),
                view = this,
                api = ReportAdd.ApiProvider.get(),
                repository = LastSelectedProjectRepositoryProvider.get(),
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    private var selectedProject: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        reportAddDescriptionLayout.requestFocus()
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportAddHours.setOnTouchListener { _, _ -> reportAddHours.text = null; false }
        reportAddDate.setOnClickListener {
            showDateDialog(supportFragmentManager) {
                controller.onDateChanged(it)
            }
        }
    }

    override fun reportTypeChanges(): Observable<ReportType> = bottomNavigation.itemSelections().map { it.itemId.toReportType() }

    override fun showDate(date: String) {
        reportAddDate.setText(date)
    }

    override fun showLoader() = showLoader(reportAddCoordinator)

    override fun hideLoader() = hideLoader(reportAddCoordinator)

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportAddCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun addReportClicks(): Observable<ReportViewModel> = toolbar.itemClicks().map {
        getReportViewModel(
                actionId = bottomNavigation.menu.checkedItemId,
                project = selectedProject,
                date = reportAddDate.text.toString(),
                hours = reportAddHours.text.toString(),
                description = reportAddDescription.text.toString())
    }

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
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
        reportAddProjectName.setText(project.name)
        selectedProject = project
    }

    override fun openProjectChooser() {
        ProjectChooseActivity.startForResult(this, REQUEST_CODE)
    }

    override fun showEmptyDescriptionError() {
        Snackbar.make(reportAddCoordinator, R.string.empty_description_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showEmptyProjectError() {
        Snackbar.make(reportAddCoordinator, R.string.empty_project_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showRegularForm() {
        reportAddDescriptionLayout.show()
        reportAddProjectNameLayout.show()
        reportAddHoursLayout.show()
        reportAddAdditionalInfo.hide()
    }

    override fun showPaidVacationsForm() {
        reportAddAdditionalInfo.hide()
        reportAddHoursLayout.show()
        reportAddProjectNameLayout.hide()
        reportAddDescriptionLayout.hide()
    }

    override fun showUnpaidVacationsForm() {
        hideRegularReportInputs()
        showAdditionalInfo(R.string.report_add_unpaid_vacations_info)
    }

    override fun showSickLeaveForm() {
        hideRegularReportInputs()
        showAdditionalInfo(R.string.report_add_sick_leave_info)
    }

    override fun showPaidConferenceForm() {

    }

    private fun hideRegularReportInputs() {
        reportAddProjectNameLayout.hide()
        reportAddDescriptionLayout.hide()
        reportAddHoursLayout.hide()
    }

    private fun showAdditionalInfo(additionalInfoResourceId: Int) {
        reportAddAdditionalInfo.show()
        reportAddAdditionalInfo.setText(additionalInfoResourceId)
    }

    override fun projectClickEvents(): Observable<Unit> {
        return RxView.clicks(reportAddProjectName).map { Unit }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            controller.onProjectChanged(ProjectChooseActivity.getProject(data))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleClickOnBackArrowItem(item)

    companion object {
        private val ADD_DATE_KEY = "dateKey"
        private val REQUEST_CODE = 555

        fun startForResult(activity: Activity, date: String, requestCode: Int) {
            activity.startActivityForResult(intent(activity, date), requestCode)
        }

        fun intent(context: Context) = Intent(context, ReportAddActivity::class.java)

        fun intent(context: Context, date: String) = intent(context).apply { putExtra(ADD_DATE_KEY, date) }
    }
}