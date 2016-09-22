package pl.elpassion.project.choose

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.project.dto.Project
import pl.elpassion.project.dto.newProject

class ProjectChooseControllerTest {

    val view = mock<ProjectChoose.View>()
    val repository = mock<ProjectChoose.Repository>()
    val controller = ProjectChooseController(view, repository)

    @Test
    fun shouldShowPossibleProjects() {
        stubRepositoryToReturn(emptyList())
        controller.onCreate()
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormRepository() {
        val projects = listOf(newProject())
        stubRepositoryToReturn(projects)
        controller.onCreate()
        verify(view).showPossibleProjects(projects)
    }

    @Test
    fun shouldSelectClickedProject() {
        val project = newProject()
        controller.onProjectClicked(project)
        verify(view).selectProject(project)
    }

    private fun stubRepositoryToReturn(list: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(list)
    }
}

interface ProjectChoose {
    interface View {
        fun showPossibleProjects(projects: List<Project>)

        fun selectProject(project: Project)
    }

    interface Repository {
        fun getPossibleProjects(): List<Project>
    }
}

class ProjectChooseController(val view: ProjectChoose.View, val repository: ProjectChoose.Repository) {
    fun onCreate() {
        view.showPossibleProjects(repository.getPossibleProjects())
    }

    fun onProjectClicked(project: Project) {
        view.selectProject(project)
    }
}
