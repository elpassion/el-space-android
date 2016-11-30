package pl.elpassion.report.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.report.Report

class ReportEditActivity : AppCompatActivity(), ReportEdit.View {
    private val report by lazy { intent.getSerializableExtra(REPORT_KEY) as Report }
    private val controller by lazy {
        ReportEditController(this, ReportEdit.EditApiProvider.get())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        controller.onCreate(report)
        reportEditHours.setOnTouchListener { view, motionEvent -> reportEditHours.text = null;false }
        reportEditProjectName.setOnClickListener {
            controller.onChooseProject()
        }
        reportEditSaveButton.setOnClickListener { controller.onSaveReport(reportEditHours.text.toString(), reportEditDescription.text.toString()) }
    }

    override fun showReport(report: Report) {
        reportEditDate.text = getPerformedAtString(report.year, report.month, report.day)
        reportEditProjectName.text = report.projectName
        reportEditHours.setText("${report.reportedHours}")
        reportEditDescription.setText(report.description)
    }

    override fun close() {
    }

    override fun showError(ex: Throwable) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun hideLoader() {
    }

    override fun showLoader() {
    }

    override fun updateProjectName(projectName: String) {
        reportEditProjectName.text = projectName
    }

    override fun openChooseProjectScreen() {
        ProjectChooseActivity.startForResult(this, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            controller.onSelectProject(ProjectChooseActivity.getProject(data!!))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val REPORT_KEY = "report_key"
        private val REQUEST_CODE = 10001

        fun intent(report: Report) = Intent().apply {
            putExtra(REPORT_KEY, report)
        }
    }
}