package pl.elpassion.report.list

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.isNotDisplayed
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.InitIntentsRule
import pl.elpassion.common.checkIntent
import pl.elpassion.common.hasChildWithText
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.common.ProjectRepository
import pl.elpassion.project.common.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.add.ReportAddActivity
import rx.Observable

class ReportListActivityHappyTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ReportListActivity>(ReportListActivity::class.java) {
        override fun beforeActivityLaunched() {
            ProjectRepositoryProvider.override = { mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
            stubCurrentTime(year = 2016, month = 10, day = 2)
            whenever(service.getReports()).thenReturn(Observable.just(listOf(newReport(year = 2016, month = 10, day = 1, projectName = "Project", description = "Description", reportedHours = 8.0))))
            ReportList.ServiceProvider.override = { service }
        }
    }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @Test
    fun shouldNotShowErrorOnView() {
        onId(R.id.reportListError).isNotDisplayed()
    }

    @Test
    fun shouldShowDayFirstOnContainer() {
        onId(R.id.reportsContainer).hasChildWithText("1")
    }

    @Test
    fun shouldShowCorrectTotalHoursInEveryDay() {
        onId(R.id.reportsContainer).hasChildWithText("Total: 8.0 hours")
    }

    @Test
    fun shouldShowReportOnContainer() {
        onId(R.id.reportsContainer).hasChildWithText("8.0h - Project")
        onId(R.id.reportsContainer).hasChildWithText("Description")
    }

    @Test
    fun shouldOpenAddReportScreenOnDayClick() {
        onText("1").click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldHaveOneDayWithMissingStatus() {
        onId(R.id.reportsContainer).hasChildWithText("MISSING")
    }

}

