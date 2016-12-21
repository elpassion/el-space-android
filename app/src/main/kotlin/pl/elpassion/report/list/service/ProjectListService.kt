package pl.elpassion.report.list.service

import pl.elpassion.project.Project
import rx.Observable

interface ProjectListService {
    fun getProjects(): Observable<List<Project>>
}