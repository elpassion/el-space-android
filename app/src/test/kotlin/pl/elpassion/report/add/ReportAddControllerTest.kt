package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository
import pl.elpassion.project.dto.newProject

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ProjectRepository>()
    val controller = ReportAddController(view, api)

    @Test
    fun shouldShowPossibleProjects() {
        val projects = listOf(newProject())
        stubApiToReturn(projects)
        controller.onCreate()
        verify(view).showSelectedProject(projects.first())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(newProject("id2", "name2"), newProject())
        stubApiToReturn(projects)
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

    private fun stubApiToReturn(list: List<Project>) {
        whenever(api.getPossibleProjects()).thenReturn(list)
    }
}

