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
            fun sendAddReport(description: String, hours: String)
        }

        interface PaidVacations {
            fun sendAddReport(hours: String)
        }

        interface SickLeave {
            fun reportSickLeave()
        }

        interface UnpaidVacations {
            fun reportUnpaidVacations()
        }
    }

    interface Controller {
        fun onReportAdded()
    }
}
