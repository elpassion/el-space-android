package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class ReportControllerTest {

    val view = mock<Report.View>()
    val api = mock<Report.Api>()

    @Test
    fun shouldShowPossibleProjects() {
        stubApiToReturn(emptyList())
        ReportController(view, api).onCreate()
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(Project())
        stubApiToReturn(projects)
        ReportController(view, api).onCreate()
        verify(view).showPossibleProjects(projects)
    }

    private fun stubApiToReturn(list: List<Project>) {
        whenever(api.getPossibleProjects()).thenReturn(list)
    }
}

interface Report {
    interface View {
        fun showPossibleProjects(projects: List<Project>)
    }

    interface Api {
        fun getPossibleProjects(): List<Project>
    }
}

class Project {

}

class ReportController(val view: Report.View, val api: Report.Api) {
    fun onCreate() {
        view.showPossibleProjects(api.getPossibleProjects())
    }
}
