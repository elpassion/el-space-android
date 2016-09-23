package pl.elpassion.report.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R

class ReportListActivity : AppCompatActivity(), ReportList.View {

    val controller by lazy { ReportListController(ReportList.ServiceProvider.get(), this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_list_activity)
        controller.onCreate()
    }


    override fun openEditReportScreen(report: Report) {

    }

    override fun showMonthName(monthName: String) {
    }

    override fun openAddReportScreen(date: String) {
    }

    override fun hideLoader() {
    }

    override fun showLoader() {
    }

    override fun showError() {
        reportListError.visibility = VISIBLE
    }

    override fun showDays(reports: List<Day>) {
    }

}