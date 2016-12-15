package pl.elpassion.project.choose

import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import rx.Observable

class ProjectChooseController(val view: ProjectChoose.View, val repository: ProjectRepository) {
    private val projectsObservable: Observable<List<Project>> =
            repository.getProjects()
                    .map {
                        it.sortedBy { it.name }
                    }.cache()

    fun onCreate() {
        projectsObservable.subscribe {
            view.showPossibleProjects(it)
        }
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }

    fun searchQuery(query: Observable<CharSequence>) {
        query.map { it.toString() }
                .switchMap { filterProjectByQuery(it) }
                .subscribe { view.showFilteredProjects(it) }
    }

    private fun filterProjectByQuery(query: String) = projectsObservable
            .flatMapIterable { it }
            .filter { it.name.contains(query, true) }
            .toList()
}