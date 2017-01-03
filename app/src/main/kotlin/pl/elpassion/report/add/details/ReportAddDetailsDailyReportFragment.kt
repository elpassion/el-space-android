package pl.elpassion.report.add.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.elpassion.R
import pl.elpassion.common.inflate

class ReportAddDetailsDailyReportFragment(override val controller: ReportAddDetails.Controller) : ReportAddDetailsFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(container!!, R.layout.report_add_details_form_daily_report)
    }
}