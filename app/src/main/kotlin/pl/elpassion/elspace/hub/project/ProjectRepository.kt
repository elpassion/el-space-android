package pl.elpassion.elspace.hub.project

import io.reactivex.Observable

interface ProjectRepository {
    fun getProjects(): Observable<List<Project>>
}
