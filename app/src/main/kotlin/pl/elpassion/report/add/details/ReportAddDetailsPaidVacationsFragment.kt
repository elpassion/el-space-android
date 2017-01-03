package pl.elpassion.report.add.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.report_add_details_form_paid_vacations.view.*
import pl.elpassion.R
import pl.elpassion.common.inflate
import pl.elpassion.report.add.ReportAdd

class ReportAddDetailsPaidVacationsFragment : ReportAddDetails.View(), ReportAdd.View.PaidVacations {
    override var controller: ReportAddDetails.Controller? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(container!!, R.layout.report_add_details_form_paid_vacations)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = ReportAddDetailsPaidVacationsController(this, activity as ReportAdd.Sender.PaidVacations)
    }

    override fun getHours(): String = view?.reportAddHours?.text.toString()
}

