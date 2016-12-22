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
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.report.HoursReport
import pl.elpassion.report.datechooser.showDateDialog


class ReportEditActivity : AppCompatActivity(), ReportEdit.View {
    private val report by lazy { intent.getSerializableExtra(REPORT_KEY) as HoursReport }
    private val controller by lazy {
        ReportEditController(this, ReportEdit.EditApiProvider.get(), ReportEdit.RemoveApiProvider.get())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        controller.onCreate(report)
        showBackArrowOnActionBar()
        reportEditDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
        reportEditHours.setOnTouchListener { view, motionEvent -> reportEditHours.text = null;false }
        reportEditProjectName.setOnClickListener {
            controller.onChooseProject()
        }
        reportEditSaveButton.setOnClickListener { controller.onSaveReport(reportEditHours.text.toString(), reportEditDescription.text.toString()) }
    }

    override fun showReport(report: HoursReport) {
        reportEditProjectName.text = report.project?.name
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
        menuInflater.inflate(R.menu.edit_raport_menu, menu)
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

        fun intent(context: Context, report: HoursReport) = Intent(context, ReportEditActivity::class.java).apply {
            putExtra(REPORT_KEY, report)
        }

        fun startForResult(activity: Activity, report: HoursReport, requestCode: Int) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }
    }
}