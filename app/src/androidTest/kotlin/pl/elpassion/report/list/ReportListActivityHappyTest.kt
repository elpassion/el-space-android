package pl.elpassion.report.list

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not
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
import pl.elpassion.startActivity
import rx.Observable

class ReportListActivityHappyTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ReportListActivity>(ReportListActivity::class.java, false, false) {
        override fun beforeActivityLaunched() {
            ProjectRepositoryProvider.override = { mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
            stubCurrentTime(year = 2016, month = 10, day = 4)
            whenever(service.getReports()).thenReturn(Observable.just(listOf(newReport(year = 2016, month = 10, day = 3, projectName = "Project", description = "Description", reportedHours = 8.0))))
            ReportList.ServiceProvider.override = { service }
        }
    }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @Test
    fun shouldNotShowErrorOnView() {
        rule.startActivity()
        onId(R.id.reportListError).isNotDisplayed()
    }

    @Test
    fun shouldShowDayFirstOnContainer() {
        rule.startActivity()
        onId(R.id.reportsContainer).hasChildWithText("1")
    }

    @Test
    fun shouldShowCorrectTotalHoursInEveryDay() {
        rule.startActivity()
        onId(R.id.reportsContainer).hasChildWithText("Total: 8.0 hours")
    }

    @Test
    fun shouldShowReportOnContainer() {
        rule.startActivity()
        onId(R.id.reportsContainer).hasChildWithText("8.0h - Project")
        onId(R.id.reportsContainer).hasChildWithText("Description")
    }

    @Test
    fun shouldOpenAddReportScreenOnDayClick() {
        rule.startActivity()
        onText("1").click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldHaveOneDayWithMissingStatus() {
        rule.startActivity()
        onId(R.id.reportsContainer).hasChildWithText(R.string.report_missing)
    }

    @Test
    fun shouldNotHaveMissingInformationOnWeekendDays() {
        rule.startActivity()
        onView(allOf(hasDescendant(withText("2")), withId(R.id.weekendDay))).check(matches(not(withText(R.string.report_missing))))
    }

}

