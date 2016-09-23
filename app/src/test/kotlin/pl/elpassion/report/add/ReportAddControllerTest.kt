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

    @Test
    fun shouldShowPossibleProjects() {
        val projects = listOf(newProject())
        stubApiToReturn(projects)
        ReportAddController(view, api).onCreate()
        verify(view).showSelectedProject(projects.first())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(newProject("id2", "name2"), newProject())
        stubApiToReturn(projects)
        ReportAddController(view, api).onCreate()
        verify(view).showSelectedProject(projects.first())
    }

    private fun stubApiToReturn(list: List<Project>) {
        whenever(api.getPossibleProjects()).thenReturn(list)
    }
}

interface ReportAdd {
    interface View {
        fun showSelectedProject(projects: Project)
    }
}

class ReportAddController(val view: ReportAdd.View, val api: ProjectRepository) {
    fun onCreate() {
        view.showSelectedProject(api.getPossibleProjects().first())
    }
}
