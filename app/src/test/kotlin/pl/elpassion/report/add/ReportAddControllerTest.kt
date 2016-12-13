package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.dto.newProject
import rx.Observable

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()
    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(Observable.just(Unit))
        stubRepositoryToReturn()
    }

    @Test
    fun shouldShowPossibleProjects() {
        createController().onCreate()
        verify(view).showSelectedProject(newProject())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(newProject("id2", "name2"), newProject())
        stubRepositoryToReturn(projects)
        val controller = createController()

        controller.onCreate()

        verify(view).showSelectedProject(projects.first())
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
        whenever(api.addReport("2016-09-23", "id", "8", "description")).thenReturn(Observable.error(RuntimeException()))
        val controller = createController("2016-09-23")

        controller.onCreate()
        controller.onReportAdd("8", "description")
        verify(view).showError(any())
    }

    @Test
    fun shouldShowCurrentDateWhenNotDateNotSelected() {
        stubCurrentTime(2016, 2, 1)
        val controller = createController(null)
        controller.onCreate()

        verify(view).showDate("2016-02-01")
    }

    private fun createController(date: String? = "2016-01-01") = ReportAddController(date, view, repository, api)

    private fun stubRepositoryToReturn(list: List<Project> = listOf(newProject())) {
        whenever(repository.getPossibleProjects()).thenReturn(list)
    }
}

