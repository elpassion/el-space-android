package pl.elpassion.report.list.service

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.report.list.ReportList
import rx.Observable
import rx.observers.TestSubscriber

class ProjectListServiceTest {
    val subscriber = TestSubscriber<List<Project>>()

    val projectApi = mock<ReportList.ProjectApi>()
    val repository = mock<CachedProjectRepository>()
    val projectListService = ProjectListServiceImpl(projectApi, repository)

    @Test
    fun shouldSaveReturnedProjectsToRepository() {
        val projects = listOf(newProject(id = 2, name = "A"), newProject(id = 2, name = "B"))
        stubProjectApiToReturn(projects)
        projectListService.getProjects().subscribe(subscriber)

        verify(repository).saveProjects(projects)
    }

    @Test
    fun shouldSaveOnlyDistinctProjects() {
        val project = newProject(id = 2, name = "A")
        val projects = listOf(project, project)
        stubProjectApiToReturn(projects)
        projectListService.getProjects().subscribe(subscriber)

        verify(repository).saveProjects(argThat { size == 1 })
    }


    private fun stubProjectApiToReturn(projects: List<Project>) {
        whenever(projectApi.getProjects()).thenReturn(Observable.just(projects))
    }

}