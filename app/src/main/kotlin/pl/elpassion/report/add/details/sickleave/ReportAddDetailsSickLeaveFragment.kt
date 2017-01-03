package pl.elpassion.report.add.details.sickleave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.elpassion.R
import pl.elpassion.common.inflate
import pl.elpassion.report.add.details.ReportAddDetails
import pl.elpassion.report.add.details.ReportAddDetailsFragment

class ReportAddDetailsSickLeaveFragment : ReportAddDetailsFragment() {
    override val controller: ReportAddDetails.Controller by lazy { ReportAddDetailsSickLeaveController(activity as ReportAddDetails.Sender.SickLeave) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(container!!, R.layout.report_add_details_form_sick_leave)
    }
}