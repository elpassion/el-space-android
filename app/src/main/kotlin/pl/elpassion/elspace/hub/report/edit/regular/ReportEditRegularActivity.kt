package pl.elpassion.elspace.hub.report.edit.regular

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.project.choose.ProjectChooseActivity
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.datechooser.showDateDialog
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import pl.elpassion.elspace.hub.report.edit.service.ReportEditServiceImpl


class ReportEditRegularActivity : AppCompatActivity(), ReportEdit.Regular.View {
    private val report by lazy { intent.getSerializableExtra(REPORT_KEY) as RegularHourlyReport }

    private val controller by lazy {
        ReportEditRegularController(this, ReportEditServiceImpl(ReportEdit.EditApiProvider.get()), ReportEdit.RemoveApiProvider.get())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        controller.onCreate(report)
        showBackArrowOnActionBar()
        reportEditDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        reportEditHours.setOnTouchListener { _, _ -> reportEditHours.text = null;false }
        reportEditProjectName.setOnClickListener {
            controller.onChooseProject()
        }
        reportEditSaveButton.setOnClickListener { controller.onSaveReport(reportEditHours.text.toString(), reportEditDescription.text.toString()) }
    }

    override fun showReport(report: RegularHourlyReport) {
        reportEditProjectName.text = report.project.name
        reportEditHours.setText("${report.reportedHours}")
        reportEditDescription.setText(report.description)
    }

    override fun showDate(date: String) {
        reportEditDate.text = date
    }

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportEditCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showEmptyDescriptionError() {
        Snackbar.make(reportEditCoordinator, R.string.empty_description_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun hideLoader() {
        hideLoader(reportEditCoordinator)
    }

    override fun showLoader() {
        showLoader(reportEditCoordinator)
    }

    override fun updateProjectName(projectName: String) {
        reportEditProjectName.text = projectName
    }

    override fun openChooseProjectScreen() {
        ProjectChooseActivity.startForResult(this, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller.onSelectProject(ProjectChooseActivity.getProject(data!!))
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        private val REQUEST_CODE = 10001

        fun intent(context: Context, report: RegularHourlyReport) = Intent(context, ReportEditRegularActivity::class.java).apply {
            putExtra(REPORT_KEY, report)
        }

        fun startForResult(activity: Activity, report: RegularHourlyReport, requestCode: Int) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }
    }
}