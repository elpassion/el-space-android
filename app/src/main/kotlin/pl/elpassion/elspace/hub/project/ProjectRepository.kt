package pl.elpassion.elspace.hub.project

import rx.Observable

interface ProjectRepository {
    fun getProjects(): Observable<List<Project>>
}
