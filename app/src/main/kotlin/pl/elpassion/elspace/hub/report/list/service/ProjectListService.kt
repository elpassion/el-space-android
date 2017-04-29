package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.project.Project
import io.reactivex.Observable

interface ProjectListService {
    fun getProjects(): Observable<List<Project>>
}