package pl.elpassion.elspace.hub.project.choose

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.ProjectRepository
import rx.Observable
import rx.Subscription

class ProjectChooseController(private val view: ProjectChoose.View,
                              private val repository: ProjectRepository,
                              private val schedulers: SchedulersSupplier) {

    private var subscription: Subscription? = null

    fun onCreate(query: Observable<CharSequence>) {
        subscription = Observable.combineLatest(projectListObservable(), query) { projectList, querySequence ->
            projectList.filter { it.name.contains(querySequence, true) }
        }.subscribe({
            view.showProjects(it)
        }, {
            view.showError(it)
        })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }

    private fun projectListObservable(): Observable<List<Project>>
            = repository.getProjects()
            .subscribeOn(schedulers.subscribeOn)
            .observeOn(schedulers.observeOn)
            .map { it.sortedBy { it.name } }
            .doOnSubscribe { view.showLoader() }
            .doOnUnsubscribe { view.hideLoader() }
}