package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.project.dto.Project
import pl.elpassion.project.dto.newProject

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()

    @Test
    fun shouldShowPossibleProjects() {
        val projects = listOf(newProject())
        stubApiToReturn(projects)
        ReportAddController(view, api).onCreate()
        verify(view).showPossibleProjects(projects)
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(newProject("id2", "name2"), newProject())
        stubApiToReturn(projects)
        ReportAddController(view, api).onCreate()
        verify(view).showPossibleProjects(projects)
    }

    private fun stubApiToReturn(list: List<Project>) {
        whenever(api.getPossibleProjects()).thenReturn(list)
    }
}

interface ReportAdd {
    interface View {
        fun showPossibleProjects(projects: List<Project>)
    }

    interface Api {
        fun getPossibleProjects(): List<Project>
    }
}

class ReportAddController(val view: ReportAdd.View, val api: ReportAdd.Api) {
    fun onCreate() {
        view.showPossibleProjects(api.getPossibleProjects())
    }
}
