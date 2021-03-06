package pl.elpassion.elspace.hub.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.hub.project.CachedProjectRepository
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.choose.ProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.report.list.ReportList

class ReportAddActivityChoosingProjectTest {

    val repository = mock<CachedProjectRepository>()

    @JvmField @Rule
    val rule = rule<ReportAddActivity>(autoStart = false) {
        ReportList.ProjectApiProvider.override = { mock() }
    }

    @Test
    fun shouldChangeSelectedProject() {
        stubRepositoryAndStart(listOf(newProject(), newProject(2, "name2")))
        Espresso.closeSoftKeyboard()
        onId(R.id.reportAddProjectName).click()
        onText("name2").click()
        onId(R.id.reportAddProjectName).hasText("name2")
    }

    private fun stubRepositoryAndStart(listOf: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(listOf)
        whenever(repository.hasProjects()).thenReturn(true)
        CachedProjectRepositoryProvider.override = { repository }
        //TODO: Make tests independent
        ProjectRepositoryProvider.override = null
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), "2016-01-01"))
    }
}

