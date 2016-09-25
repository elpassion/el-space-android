package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.dto.newProject
import rx.Observable

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()
    val repository = mock<ProjectRepository>()
    val controller = ReportAddController(view, repository, api)

    @Before
    fun setUp() {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(Observable.just(Unit))
        stubRepositoryToReturn()
    }

    @Test
    fun shouldShowPossibleProjects() {
        onCreate()
        verify(view).showSelectedProject(newProject())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(newProject("id2", "name2"), newProject())
        stubRepositoryToReturn(projects)
        onCreate()
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
        onCreate()
        controller.onReportAdd("8", "description")
        verify(view).close()
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(Observable.error(RuntimeException()))
        onCreate()
        controller.onReportAdd("8", "description")
        verify(view).showError(any())
    }

    @Test
    fun shouldShowDate() {
        onCreate("2016-09-23")
        verify(view).showDate("2016-09-23")
    }
    @Test
    fun shouldUseApi() {
        whenever(api.addReport("2016-09-23", "id", "8", "description")).thenReturn(Observable.error(RuntimeException()))
        onCreate("2016-09-23")
        controller.onReportAdd("8", "description")
        verify(view).showError(any())
    }

    private fun onCreate(date: String = "2016-01-01") {
        controller.onCreate(date)
    }

    private fun stubRepositoryToReturn(list: List<Project> = listOf(newProject())) {
        whenever(repository.getPossibleProjects()).thenReturn(list)
    }
}

