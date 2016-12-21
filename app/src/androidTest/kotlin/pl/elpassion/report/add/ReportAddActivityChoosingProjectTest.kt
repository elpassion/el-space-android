package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule
import pl.elpassion.project.Project
import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.project.CachedProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class ReportAddActivityChoosingProjectTest {

    val repository = mock<CachedProjectRepository>()

    @JvmField @Rule
    val rule = rule<ReportAddActivity>(autoStart = false)

    @Test
    fun shouldChangeSelectedProject() {
        stubRepositoryAndStart(listOf(newProject(), newProject(2, "name2")))
        onId(R.id.reportAddProjectName).click()
        onText("name2").click()
        onId(R.id.reportAddProjectName).hasText("name2")
    }

    private fun stubRepositoryAndStart(listOf: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(listOf)
        CachedProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), "2016-01-01"))
    }
}

