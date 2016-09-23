package pl.elpassion.report.add

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository
import pl.elpassion.project.common.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class ReportAddActivityChoosingProjectTest {

    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val rule = ActivityTestRule<ReportAddActivity>(ReportAddActivity::class.java, false, false)

    @Test
    fun shouldChangeSelectedProject() {
        stubRepository(listOf(newProject(), newProject("id2", "name2")))
        rule.startActivity()
        onId(R.id.reportAddProjectName).click()
        onText("name2").click()
        onId(R.id.reportAddProjectName).hasText("name2")
    }

    private fun stubRepository(listOf: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(listOf)
        ProjectRepositoryProvider.override = { repository }
    }
}

