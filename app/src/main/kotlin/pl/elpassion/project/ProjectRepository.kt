package pl.elpassion.project

import rx.Observable

interface ProjectRepository {
    fun getProjects(): Observable<List<Project>>
}
