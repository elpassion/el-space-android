package pl.elpassion.project.choose

import pl.elpassion.api.applySchedulers
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import rx.Observable
import rx.Subscription

class ProjectChooseController(private val view: ProjectChoose.View,
                              private val repository: ProjectRepository) {

    private var subscription: Subscription? = null

    fun onCreate(query: Observable<CharSequence>) {
        subscription = Observable.combineLatest(projectListObservable(), query) { projectList, querySequence ->
            projectList.filter { it.name.contains(querySequence, true) }
        }.subscribe({
            view.showPossibleProjects(it)
        }, {
            view.showError()
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
            .map { it.sortedBy { it.name } }
            .applySchedulers()
            .doOnSubscribe { view.showLoader() }
            .doOnUnsubscribe { view.hideLoader() }
}