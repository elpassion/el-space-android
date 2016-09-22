package pl.elpassion.project.choose

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class ProjectChooseControllerTest {
    val view = mock<ProjectChoose.View>()
    val api = mock<ProjectChoose.Api>()

    @Test
    fun shouldShowPossibleProjects() {
        stubApiToReturn(emptyList())
        ProjectChooseController(view, api).onCreate()
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormApi() {
        val projects = listOf(Project())
        stubApiToReturn(projects)
        ProjectChooseController(view, api).onCreate()
        verify(view).showPossibleProjects(projects)
    }

    @Test
    fun shouldSelectClickedProject() {
        val project = Project()
        ProjectChooseController(view, api).onProjectClicked(project)
        verify(view).selectProject(project)
    }

    private fun stubApiToReturn(list: List<Project>) {
        whenever(api.getPossibleProjects()).thenReturn(list)
    }
}

interface ProjectChoose {
    interface View {
        fun showPossibleProjects(projects: List<Project>)

        fun selectProject(project: Project)
    }

    interface Api {
        fun getPossibleProjects(): List<Project>
    }
}

class Project {

}

class ProjectChooseController(val view: ProjectChoose.View, val api: ProjectChoose.Api) {
    fun onCreate() {
        view.showPossibleProjects(api.getPossibleProjects())
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }
}
