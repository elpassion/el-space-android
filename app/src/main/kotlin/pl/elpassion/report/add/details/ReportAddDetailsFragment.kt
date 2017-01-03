package pl.elpassion.report.add.details

import android.support.v4.app.Fragment

abstract class ReportAddDetailsFragment : Fragment(), ReportAddDetails.View {
    abstract val controller: ReportAddDetails.Controller?
}