package pl.elpassion.elspace.hub.report.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.daily_report_edit_activity.*
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportType
import rx.Observable

class ReportEditActivity : AppCompatActivity(), ReportEdit.View {

    private val controller by lazy { ReportEditController(intent.getSerializableExtra(REPORT_KEY) as Report, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        setSupportActionBar(toolbar)
        controller.onCreate()
    }

    override fun showDate(date: String) {
        reportEditDate.text = date
    }

    override fun showReportedHours(reportedHours: Double) = Unit

    override fun showProjectName(name: String) = Unit

    override fun showDescription(description: String) = Unit

    override fun reportTypeChanges(): Observable<ReportType> = Observable.never()

    override fun showRegularForm() = Unit

    override fun showPaidVacationsForm() = Unit

    override fun showSickLeaveForm() = Unit

    override fun showUnpaidVacationsForm() = Unit

    companion object {

        private val REPORT_KEY = "report_key"

        fun startForResult(activity: Activity, requestCode: Int, report: Report) {
            activity.startActivityForResult(intent(activity, report), requestCode)
        }

        fun intent(context: Context, report: Report) = intent(context).apply { putExtra(REPORT_KEY, report) }

        private fun intent(context: Context) = Intent(context, ReportEditActivity::class.java)
    }
}