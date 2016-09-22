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

class ProjectChooseActivityTest {

    val repository = mock<ProjectChoose.Repository>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ProjectChooseActivity>(ProjectChooseActivity::class.java) {
        override fun beforeActivityLaunched() {
            ProjectChoose.RepositoryProvider.override = { repository }
        }
    }

    @Test
    fun shouldDisplayProjectFromRepository() {
        stubRepositoryToReturn(listOf(newProject()))
        onText("name").isDisplayed()
    }

    private fun stubRepositoryToReturn(projects: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(projects)
    }
}

