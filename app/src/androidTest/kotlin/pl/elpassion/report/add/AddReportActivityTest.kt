package pl.elpassion.report.add

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.project.choose.ProjectChoose
import pl.elpassion.project.dto.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class AddReportActivityTest {

    val repository = mock<ProjectChoose.Repository>()

    @JvmField @Rule
    val rule = ActivityTestRule<AddReportActivity>(AddReportActivity::class.java, false, false)

    @Test
    fun shouldStartWithFirstProjectSelected() {
        stubRepository(listOf(newProject()))
        rule.startActivity()
        onText("name").isDisplayed()
    }

    private fun stubRepository(listOf: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(listOf)
        ProjectChoose.RepositoryProvider.override = { repository }
    }
}

