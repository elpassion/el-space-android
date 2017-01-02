package pl.elpassion.report.add

import android.support.v4.app.Fragment

interface ReportAddDetails {

    abstract class View : Fragment() {
        abstract val controller: Controller?
    }

    interface Controller {
        fun onReportAdded()
    }
}
