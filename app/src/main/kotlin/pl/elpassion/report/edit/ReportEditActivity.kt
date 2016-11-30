package pl.elpassion.report.edit

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.report.Report
import rx.Observable

class ReportEditActivity : AppCompatActivity(), ReportEdit.View {
    private val report by lazy { intent.getSerializableExtra(REPORT_KEY) as Report }
    private val controller by lazy {
        ReportEditController(this, object : ReportEdit.EditApi {
            override fun editReport(id: Long, date: String, reportedHour: Double, description: String, projectId: String): Observable<Unit> {
                throw UnsupportedOperationException("not implemented")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        controller.onCreate(report)
        reportEditHours.setOnTouchListener { view, motionEvent -> reportEditHours.text = null;false }
        reportEditProjectName.setOnClickListener {
            controller.onChooseProject()
        }
    }

    override fun showReport(report: Report) {
        reportEditDate.text = getPerformedAtString(report.year, report.month, report.day)
        reportEditProjectName.text = report.projectName
        reportEditHours.setText("${report.reportedHours}")
        reportEditDescription.setText(report.description)
    }

    override fun close() {
        throw UnsupportedOperationException("not implemented")
    }

    override fun showError(ex: Throwable) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun hideLoader() {
        throw UnsupportedOperationException("not implemented")
    }

    override fun showLoader() {
        throw UnsupportedOperationException("not implemented")
    }

    override fun updateProjectName(projectName: String) {

    }

    override fun openChooseProjectScreen() {
        ProjectChooseActivity.startForResult(this, 1)
    }

    companion object {
        private val REPORT_KEY = "report_key"

        fun intent(report: Report) = Intent().apply {
            putExtra(REPORT_KEY, report)
        }
    }
}