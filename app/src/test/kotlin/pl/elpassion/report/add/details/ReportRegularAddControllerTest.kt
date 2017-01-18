package pl.elpassion.report.add.details

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.last.LastSelectedProjectRepository
import pl.elpassion.report.add.details.regular.ReportAddDetailsRegularController
import rx.subjects.PublishSubject

class ReportRegularAddControllerTest {

    val view = mock<ReportAddDetails.View.Regular>()
    val sender = mock<ReportAddDetails.Sender.Regular>()
    val repository = mock<LastSelectedProjectRepository>()
    val controller = createController()
    private val projectChanges = PublishSubject.create<Project>()
    private val projectClickEvents = PublishSubject.create<Unit>()

    @Before
    fun setUp() {
        whenever(view.projectChanges()).thenReturn(projectChanges)
        whenever(view.projectClickEvents()).thenReturn(projectClickEvents)
    }

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
        controller.onCreate()
        projectClickEvents.onNext(Unit)
        verify(view).openProjectChooser()
    }

    @Test
    fun shouldShowSelectedProject() {
        controller.onCreate()
        projectChanges.onNext(newProject())
        verify(view).showSelectedProject(newProject())
    }

    @Test
    fun shouldCallSenderAfterOnReportAdded() {
        whenever(view.getDescription()).thenReturn("description")
        whenever(view.getHours()).thenReturn("8")
        controller.onCreate()

        projectChanges.onNext(newProject(id = 1))
        controller.onReportAdded()
        verify(sender).addRegularReport("description", "8", projectId = 1)
    }

    @Test
    fun shouldReallyCallSenderAfterOnReportAdded() {
        whenever(view.getDescription()).thenReturn("description2")
        whenever(view.getHours()).thenReturn("9")
        controller.onCreate()

        projectChanges.onNext(newProject(id = 2))
        controller.onReportAdded()
        verify(sender).addRegularReport("description2", "9", projectId = 2)
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmpty() {
        whenever(view.getDescription()).thenReturn("")
        controller.onCreate()

        controller.onReportAdded()
        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldShowEmptyProjectErrorWhenProjectWasNotSelected() {
        whenever(view.getDescription()).thenReturn("description")
        controller.onCreate()

        controller.onReportAdded()
        verify(view).showEmptyProjectError()
    }

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun createController() = ReportAddDetailsRegularController(view, sender, repository)
}