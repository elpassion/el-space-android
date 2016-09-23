package pl.elpassion.report.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.VISIBLE
import android.widget.TextView
import com.elpassion.android.commons.recycler.BaseRecyclerViewAdapter
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R

class ReportListActivity : AppCompatActivity(), ReportList.View {

    val controller by lazy { ReportListController(ReportList.ServiceProvider.get(), this) }

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
    }

    override fun hideLoader() {
    }

    override fun showLoader() {
    }

    override fun showError() {
        reportListError.visibility = VISIBLE
    }

    override fun showDays(reports: List<Day>) {
        reportsContainer.adapter = ReportsAdapter(reports.map { ReportItemAdapter(it.dayNumber) })
    }

}

class ReportItemAdapter(val dayNumber: Int) : ItemAdapter<ReportItemAdapter.VH>(R.layout.report_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        (holder.itemView as TextView).text = dayNumber.toString()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}

class ReportsAdapter(itemAdapters: List<ItemAdapter<*>>) : BaseRecyclerViewAdapter(itemAdapters) {

}
