package pl.elpassion.elspace.hub.report.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.hub.project.choose.ProjectChooseActivity
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportType
import pl.elpassion.elspace.hub.report.datechooser.showDateDialog
import rx.Observable

class ReportEditActivity : AppCompatActivity(), ReportEdit.View {

    private val controller by lazy { ReportEditController(intent.getSerializableExtra(REPORT_KEY) as Report, this) }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_PROJECT_REQUEST_CODE && data != null) {
            controller.onProjectChanged(ProjectChooseActivity.getProject(data))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            return handleClickOnBackArrowItem(item)
        } else {
            return false
        }
    }

    override fun showDate(date: String) {
        reportEditDate.setText(date)
    }

    override fun showReportedHours(reportedHours: Double) {
        reportEditHours.setText(reportedHours.toStringWithoutZeroes())
    }

    override fun showProjectName(name: String) {
        reportEditProjectName.setText(name)
    }

    override fun showDescription(description: String) {
        reportEditDescription.setText(description)
    }

    override fun reportTypeChanges(): Observable<ReportType> = Observable.never()

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

    private fun Double.toStringWithoutZeroes() = if (this == Math.floor(this)) "%.0f".format(this) else toString()

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