package pl.elpassion.report.list

import android.support.test.espresso.Espresso
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import rx.Observable

class ReportListActivityAddReportTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ReportListActivity>(ReportListActivity::class.java) {
        override fun beforeActivityLaunched() {
            ProjectRepositoryProvider.override = { mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
            stubCurrentTime(year = 2016, month = 10, day = 1)
            whenever(service.getReports()).thenReturn(Observable.just(listOf(newReport(year = 2016, month = 10, day = 1, projectName = "Project", description = "Description", reportedHours = 8.0))))
            ReportList.ServiceProvider.override = { service }
        }
    }

    @Test
    fun shouldCloseAddReportActivity() {
        onText("1 Sat").click()
        onId(R.id.reportAddDescription).typeText("Description")
        Espresso.closeSoftKeyboard()
        onId(R.id.reportAddAdd).click()
        onId(R.id.reportsContainer).isDisplayed()
    }
}

