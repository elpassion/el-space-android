package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class ReportControllerTest {

    @Test
    fun shouldShowPossibleProjects() {
        val view = mock<Report.View>()
        val api = mock<Report.Api>()
        whenever(api.getPossibleProjects()).thenReturn(emptyList())
        ReportController(view, api).onCreate()
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val view = mock<Report.View>()
        val api = mock<Report.Api>()
        val projects = listOf(Project())
        whenever(api.getPossibleProjects()).thenReturn(projects)
        ReportController(view, api).onCreate()
        verify(view).showPossibleProjects(projects)
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
