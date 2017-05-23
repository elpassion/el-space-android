package pl.elpassion.elspace.hub.project.choose

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.ProjectRepository

class ProjectChooseController(private val view: ProjectChoose.View,
                              private val repository: ProjectRepository,
                              private val schedulers: SchedulersSupplier) {

    private var subscription: Disposable? = null

    fun onCreate(query: Observable<CharSequence>) {
        subscription = Observables.combineLatest(projectListObservable(), query, { projectList, querySequence ->
            projectList.filter { it.name.contains(querySequence, true) }
        }).subscribeBy(
                onNext = { view.showProjects(it) },
                onError = { view.showError(it) }
        )
    }

    fun onDestroy() {
        subscription?.dispose()
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
            .doFinally { view.hideLoader() }
}