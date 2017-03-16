package pl.elpassion.elspace.hub.report.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.report_edit_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportType
import rx.Observable

class ReportEditActivity : AppCompatActivity(), ReportEdit.View {

    private val controller by lazy { ReportEditController(intent.getSerializableExtra(REPORT_KEY) as Report, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        controller.onCreate()
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

    override fun showRegularForm() = Unit

    override fun showPaidVacationsForm() = Unit

    override fun showSickLeaveForm() = Unit

    override fun showUnpaidVacationsForm() = Unit

    private fun Double.toStringWithoutZeroes() = if (this == Math.floor(this)) "%.0f".format(this) else toString()

    companion object {

        private val REPORT_KEY = "report_key"

        fun startForResult(activity: Activity, requestCode: Int, report: Report) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }

        fun intent(context: Context, report: Report) = intent(context).apply { putExtra(REPORT_KEY, report) }

        private fun intent(context: Context) = Intent(context, ReportEditActivity::class.java)
    }
}