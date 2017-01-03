package pl.elpassion.report.add.details

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.last.LastSelectedProjectRepository

class ReportRegularAddControllerTest {

    val view = mock<ReportAddDetails.View.Regular>()
    val sender = mock<ReportAddDetails.Sender.Regular>()
    val repository = mock<LastSelectedProjectRepository>()
    val controller = createController()

    @Test
    fun shouldShowPossibleProject() {
        val project = newProject()
        stubRepositoryToReturn(project)

        controller.onCreate()

        verify(view).showSelectedProject(project)
    }

    @Test
    fun shouldNotShowPossibleProjectWhenRepositoryReturnNull() {
        stubRepositoryToReturn(null)
        controller.onCreate()

        verify(view, never()).showSelectedProject(any())
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
    fun shouldCallSenderAfterOnReportAdded() {
        whenever(view.getDescription()).thenReturn("description")
        whenever(view.getHours()).thenReturn("8")
        controller.onReportAdded()
        verify(sender).sendAddReport("description", "8")
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmpty() {
        whenever(view.getDescription()).thenReturn("")
        controller.onReportAdded()
        verify(view).showEmptyDescriptionError()
    }

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun createController() = ReportAddDetailsRegularController(view, sender, repository)
}