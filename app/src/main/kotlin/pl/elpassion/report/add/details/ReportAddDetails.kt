package pl.elpassion.report.add.details

import pl.elpassion.project.Project

interface ReportAddDetails {

    interface View {
        interface Regular : View {
            fun showSelectedProject(project: Project)
            fun openProjectChooser()
            fun getDescription(): String
            fun showEmptyDescriptionError()
            fun getHours(): String
            fun showEmptyProjectError()
        }

        interface PaidVacations : View {
            fun getHours(): String
        }
    }

    interface Sender {
        interface Regular {
            fun addRegularReport(description: String, hours: String, projectId: Long)
        }

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
