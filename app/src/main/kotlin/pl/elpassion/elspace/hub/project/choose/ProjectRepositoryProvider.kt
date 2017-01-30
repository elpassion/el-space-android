package pl.elpassion.elspace.hub.project.choose

import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.ProjectRepository
import pl.elpassion.elspace.hub.project.ProjectRepositoryImpl
import pl.elpassion.elspace.hub.report.list.ReportList

object ProjectRepositoryProvider : Provider<ProjectRepository>({
    ProjectRepositoryImpl(ReportList.ProjectListServiceProvider.get(), CachedProjectRepositoryProvider.get())
})