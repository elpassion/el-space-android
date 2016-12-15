package pl.elpassion.project.choose

import pl.elpassion.common.Provider
import pl.elpassion.project.CachedProjectRepositoryProvider
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryImpl
import pl.elpassion.report.list.ReportList

object ProjectRepositoryProvider : Provider<ProjectRepository>({
    ProjectRepositoryImpl(ReportList.ProjectApiProvider.get(), CachedProjectRepositoryProvider.get())
})