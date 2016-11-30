package pl.elpassion.report.edit

import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.project.Project
import pl.elpassion.report.Report
import rx.Subscription
import kotlin.properties.Delegates

class ReportEditController(val view: ReportEdit.View, val editReportApi: ReportEdit.EditApi) {

    private var reportId: Long by Delegates.notNull()
    private lateinit var reportDate: String
    private lateinit var projectId: String
    private var subscription: Subscription? = null

    fun onCreate(report: Report) {
        reportId = report.id
        reportDate = getPerformedAtString(report.year, report.month, report.day)
        projectId = "${report.projectId}"
        view.showReport(report)
    }

    fun onChooseProject() {
        view.openChooseProjectScreen()
    }

    fun onSaveReport(hours: String, description: String) {
        subscription = editReportApi.editReport(id = reportId, date = reportDate, reportedHour = hours, description = description, projectId = projectId)
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }

    fun onSelectProject(project: Project) {
        projectId = project.id
        view.updateProjectName(project.name)
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

}