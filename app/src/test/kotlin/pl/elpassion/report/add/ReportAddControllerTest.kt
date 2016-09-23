package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository
import pl.elpassion.project.dto.newProject
import rx.Observable

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()
    val repository = mock<ProjectRepository>()
    val controller = ReportAddController(view, repository, api)

    @Before
    fun setUp() {
        whenever(api.addReport()).thenReturn(Observable.just(Unit))
    }

    @Test
    fun shouldShowPossibleProjects() {
        val projects = listOf(newProject())
        stubRepositoryToReturn(projects)
        controller.onCreate()
        verify(view).showSelectedProject(projects.first())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(newProject("id2", "name2"), newProject())
        stubRepositoryToReturn(projects)
        controller.onCreate()
        verify(view).showSelectedProject(projects.first())
    }

    @Test
    fun shouldOpenProjectChooserOnProjectClicked() {
        controller.onProjectClicked()
        verify(view).openProjectChooser()
    }

    @Test
    fun shouldShowSelectedProject() {
        controller.onSelectProject(newProject())
        verify(view).showSelectedProject(newProject())
    }

    @Test
    fun shouldCloseAfterAddingNewReport() {
        controller.onReportAdd("8", "description")
        verify(view).close()
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        whenever(api.addReport()).thenReturn(Observable.error(RuntimeException()))
        controller.onReportAdd("8", "description")
        verify(view).showError()
    }

    private fun stubRepositoryToReturn(list: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(list)
    }
}

