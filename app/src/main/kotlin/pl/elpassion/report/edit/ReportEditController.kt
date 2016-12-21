package pl.elpassion.report.edit

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.project.Project
import pl.elpassion.report.Report
import rx.Subscription
import kotlin.properties.Delegates

class ReportEditController(private val view: ReportEdit.View,
                           private val editReportApi: ReportEdit.EditApi,
                           private val removeReportApi: ReportEdit.RemoveApi) {

    private var reportId: Long by Delegates.notNull()
    private lateinit var reportDate: String
    private var projectId: Long by Delegates.notNull()
    private var subscription: Subscription? = null
    private var removeReportSubscription: Subscription? = null

    fun onCreate(report: Report) {
        reportId = report.id
        reportDate = getPerformedAtString(report.year, report.month, report.day)
        projectId = report.projectId
        view.showReport(report)
    }

    fun onChooseProject() {
        view.openChooseProjectScreen()
    }

    fun onSaveReport(hours: String, description: String) {
        if (description.isEmpty()) {
            view.showEmptyDescriptionError()
        } else {
            sendEditedReport(description, hours)
        }
    }

    private fun sendEditedReport(description: String, hours: String) {
        subscription = editReportApi.editReport(id = reportId, date = reportDate, reportedHour = hours, description = description, projectId = projectId)
                .applySchedulers()
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

    fun onRemoveReport() {
        removeReportSubscription = removeReportApi.removeReport(reportId)
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
        removeReportSubscription?.unsubscribe()
    }

}