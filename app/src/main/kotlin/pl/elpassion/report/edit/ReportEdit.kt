package pl.elpassion.report.edit

import pl.elpassion.api.RetrofitProvider
import pl.elpassion.common.Provider
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
        fun editReport(id: Long, date: String, reportedHour: String, description: String, projectId: String): Observable<Unit>
    }

    object EditApiProvider : Provider<EditApi>({
        RetrofitProvider.get().create(EditApi::class.java)
    })
}