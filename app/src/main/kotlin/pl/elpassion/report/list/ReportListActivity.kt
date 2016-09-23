package pl.elpassion.report.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R
import pl.elpassion.report.add.ReportAddActivity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ReportListActivity : AppCompatActivity(), ReportList.View {

    val controller by lazy { ReportListController(object : ReportList.Service {
        val service = ReportList.ServiceProvider.get()
        override fun getReports(): Observable<List<Report>> {
           return service.getReports().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }

    }, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_list_activity)
        reportsContainer.layoutManager = LinearLayoutManager(this)
        controller.onCreate()
    }

    override fun openEditReportScreen(report: Report) {

    }

    override fun showMonthName(monthName: String) {
    }

    override fun openAddReportScreen(date: String) {
        ReportAddActivity.start(this, date)
    }

    override fun hideLoader() {
    }

    override fun showLoader() {
    }

    override fun showError(it: Throwable) {
        Log.e("Error", it.message, it)
        reportListError.visibility = VISIBLE
    }

    override fun showDays(days: List<Day>, listener: OnDayClickListener) {
        reportsContainer.adapter = ReportsAdapter(days.flatMap { listOf(DayItemAdapter(it, listener)) + it.reports.map { report -> ReportItemAdapter(report) } })
    }
}

