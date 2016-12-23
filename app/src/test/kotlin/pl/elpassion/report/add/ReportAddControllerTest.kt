package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.last.LastSelectedProjectRepository
import rx.Observable

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()
    val repository = mock<LastSelectedProjectRepository>()

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubApiToReturn(Observable.just(Unit))
        stubRepositoryToReturn()
    }

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
    fun shouldCloseAfterAddingNewReport() {
        val controller = createController()
        controller.onCreate()
        controller.onReportAdd("8", "description")
        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnAddingNewReport() {
        stubApiToReturn(Observable.never())
        val controller = createController()
        controller.onCreate()
        controller.onReportAdd("8", "description")

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportFinish() {
        stubApiToReturn(Observable.just(Unit))
        val controller = createController()
        controller.onCreate()
        controller.onReportAdd("8", "description")

        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportCanceledByOnDestroy() {
        stubApiToReturn(Observable.never())
        val controller = createController()
        controller.onCreate()
        controller.onReportAdd("8", "description")
        controller.onDestroy()

        verify(view).hideLoader()
    }
    
    @Test
    fun shouldShowEmptyDescriptionError() {
        val controller = createController()
        controller.onCreate()
        controller.onReportAdd("8", "")

        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldShowSelectedDate() {
        val controller = createController()

        controller.onDateSelect("2016-05-04")

        verify(view).showDate("2016-05-04")
    }

    @Test
    fun shouldChangeDateAfterOnCreate() {
        val controller = createController("2016-01-04")
        controller.onSelectProject(newProject())

        controller.onDateSelect("2016-05-04")
        controller.onReportAdd("0.1", "Desription")

        verify(api).addReport(eq("2016-05-04"), any(), any(), any())
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(Observable.error(RuntimeException()))
        val controller = createController()
        controller.onCreate()
        controller.onReportAdd("8", "description")
        verify(view).showError(any())
    }

    @Test
    fun shouldShowDate() {
        val controller = createController("2016-09-23")
        controller.onCreate()
        verify(view).showDate("2016-09-23")
    }

    @Test
    fun shouldUseApi() {
        val exception = RuntimeException()
        val project = Project(1, "Slack")
        whenever(api.addReport("2016-09-23", project.id, "8", "description")).thenReturn(Observable.error(exception))
        val controller = createController("2016-09-23")

        controller.onSelectProject(project)
        controller.onReportAdd("8", "description")
        verify(view).showError(exception)
    }

    @Test
    fun shouldShowCurrentDateWhenNotDateNotSelected() {
        stubCurrentTime(2016, 2, 1)
        val controller = createController(null)
        controller.onCreate()

        verify(view).showDate("2016-02-01")
    }

    private fun createController(date: String? = "2016-01-01") = ReportAddController(date, view, repository, api)

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun stubApiToReturn(observable: Observable<Unit>) {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(observable)
    }
}

