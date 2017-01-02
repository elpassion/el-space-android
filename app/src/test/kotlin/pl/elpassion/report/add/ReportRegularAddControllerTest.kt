package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertFalse
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.last.LastSelectedProjectRepository

class ReportRegularAddControllerTest {

    val view = mock<ReportAdd.View.Regular>()
    val repository = mock<LastSelectedProjectRepository>()

    @Test
    fun shouldShowPossibleProject() {
        val project = newProject()
        stubRepositoryToReturn(project)
        val controller = createController()

        controller.onCreate()

        verify(view).showSelectedProject(project)
    }

    @Test
    fun shouldNotShowPossibleProjectWhenRepositoryReturnNull() {
        stubRepositoryToReturn(null)
        val controller = createController()

        controller.onCreate()

        verify(view, never()).showSelectedProject(any())
    }

    @Test
    fun shouldOpenProjectChooserOnProjectClicked() {
        createController().onProjectClicked()
        verify(view).openProjectChooser()
    }

    @Test
    fun shouldShowSelectedProject() {
        createController().onSelectProject(newProject())
        verify(view).showSelectedProject(newProject())
    }

    @Test
    fun shouldShowReturnFalseWhenDescriptionIsEmpty() {
        whenever(view.getDescription()).thenReturn("")
        val controller = createController()
        controller.onCreate()
        assertFalse(controller.isReportValid())
    }

    @Test
    fun shouldShowEmptyDescriptionErrorOnShowError() {
        val controller = createController()
        controller.onCreate()
        controller.onError()
        verify(view).showEmptyDescriptionError()
    }

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun createController() = ReportAddDetailsRegularController(view, repository)
}