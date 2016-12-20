package pl.elpassion.project.choose

import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import rx.Observable

class ProjectChooseController(private val view: ProjectChoose.View,
                              repository: ProjectRepository) {

    private val projectsObservable: Observable<Project> =
            repository.getProjects()
                    .flatMapIterable { it }
                    .sorted { project, project2 -> project.name.compareTo(project2.name) }
                    .replay()
                    .autoConnect()

    fun onCreate() {
        projectsObservable.subscribeProjects()
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }

    private fun Observable<Project>.subscribeProjects() {
        this.toList().subscribe({
            view.showPossibleProjects(it)
        }, {
            view.showError()
        })
    }

    fun searchQuery(query: Observable<CharSequence>) {
        query.map { it.toString() }
                .switchMap { filterProjectByQuery(it) }
                .subscribeProjects()
    }

    private fun filterProjectByQuery(query: String) = projectsObservable
            .filter { it.name.contains(query, true) }
}