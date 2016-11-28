package pl.elpassion.report.edit

import pl.elpassion.report.Report
import rx.Observable

interface ReportEdit {
    interface View {
        fun showReport(report: Report)
        fun openChooseProjectScreen()
        fun updateProjectName(projectName: String)
        fun showLoader()
        fun hideLoader()
        fun showError(ex: Throwable)
        fun close()
    }

    interface EditApi {
        fun editReport(id: Long, date: String, reportedHour: Double, description: String, projectId: String): Observable<Unit>
    }
}