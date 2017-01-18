package pl.elpassion.report.add.details.regular

import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepository
import pl.elpassion.report.add.details.ReportAddDetails
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class ReportAddDetailsRegularController(private val view: ReportAddDetails.View.Regular,
                                        private val sender: ReportAddDetails.Sender.Regular,
                                        private val repository: LastSelectedProjectRepository) : ReportAddDetails.Controller {

    private val subscriptions: CompositeSubscription = CompositeSubscription()
    private var selectedProject: Project? = null

    fun onCreate() {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
        }
        view.projectClickEvents()
                .subscribe {
                    view.openProjectChooser()
                }
                .save()
        view.projectChanges()
                .subscribe {
                    view.showSelectedProject(it)
                    selectedProject = it
                }
                .save()
    }

    fun onDestroy() {
        subscriptions.clear()
    }

    override fun onReportAdded() {
        if (view.getDescription().isBlank()) {
            view.showEmptyDescriptionError()
        } else if (selectedProject == null) {
            view.showEmptyProjectError()
        } else {
            sender.addRegularReport(view.getDescription(), view.getHours(), selectedProject!!.id)
        }
    }

    private fun Subscription.save() {
        subscriptions.add(this)
    }
}