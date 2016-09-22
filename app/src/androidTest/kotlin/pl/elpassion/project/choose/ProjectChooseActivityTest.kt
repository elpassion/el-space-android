package pl.elpassion.project.choose

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.project.dto.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class ProjectChooseActivityTest {

    val repository = mock<ProjectChoose.Repository>()

    @JvmField @Rule
    val rule = ActivityTestRule<ProjectChooseActivity>(ProjectChooseActivity::class.java, false, false)

    @Test
    fun shouldDisplayProjectFromRepository() {
        stubRepositoryToReturn(listOf(newProject()))
        rule.startActivity()
        onText("name").isDisplayed()
    }

    @Test
    fun shouldDisplayTwoProjectsFromRepository() {
        stubRepositoryToReturn(listOf(newProject("id1", "name1"), newProject("id2", "name2")))
        rule.startActivity()
        onText("name1").isDisplayed()
        onText("name2").isDisplayed()
    }

    private fun stubRepositoryToReturn(projects: List<Project>) {
        ProjectChoose.RepositoryProvider.override = { repository }
        whenever(repository.getPossibleProjects()).thenReturn(projects)
    }
}

