package pl.elpassion.report.add.details

import pl.elpassion.project.Project
import rx.Observable

interface ReportAddDetails {

    interface View {
        interface Regular : View {
            fun showSelectedProject(project: Project)
            fun openProjectChooser()
            fun showEmptyDescriptionError()
            fun showEmptyProjectError()
            fun projectClickEvents(): Observable<Unit>
        }

        interface PaidVacations : View {
            fun getHours(): String
        }
    }

    interface Sender {
        interface PaidVacations {
            fun addPaidVacationsReport(hours: String)
        }

        interface SickLeave {
            fun addSickLeaveReport()
        }

        interface UnpaidVacations {
            fun addUnpaidVacationsReport()
        }
    }

    interface Controller {
        fun onReportAdded()
    }
}
