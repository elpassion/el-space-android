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
import rx.Observable

class ReportListActivityHappyTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ReportListActivity>(ReportListActivity::class.java) {
        override fun beforeActivityLaunched() {
            ProjectRepositoryProvider.override = { mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
            stubCurrentTime(year = 2016, month = 10, day = 4)
            whenever(service.getReports()).thenReturn(Observable.just(listOf(
                    newReport(year = 2016, month = 10, day = 3, projectName = "Project", description = "Description", reportedHours = 8.0),
                    newReport(year = 2016, month = 10, day = 2, reportedHours = 3.0))))
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
    fun shouldShowCorrectlyDayNameOnWeekendDays() {
        onId(R.id.reportsContainer).hasChildWithText("1 Sat")
    }

    @Test
    fun shouldShowCorrectlyDayNameOnNotFilledInDays() {
        onId(R.id.reportsContainer).hasChildWithText("4 Tue")
    }

    @Test
    fun shouldShowCorrectlyDayNameOnNormalDays() {
        onId(R.id.reportsContainer).hasChildWithText("3 Mon")
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
        onText("1 Sat").click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldHaveOneDayWithMissingStatus() {
        onId(R.id.reportsContainer).hasChildWithText(R.string.report_missing)
    }

    @Test
    fun shouldNotHaveMissingInformationOnWeekendDays() {
        onView(allOf(hasDescendant(withText("2 Sun")), withParent(withId(R.id.reportsContainer)))).check(matches(not(hasDescendant(withText(R.string.report_missing)))))
    }

    @Test
    fun shouldNotHaveTotalInformationOnNotPassedDays() {
        onView(allOf(hasDescendant(withText("5 Wed")), withParent(withId(R.id.reportsContainer)))).check(matches(not(hasDescendant(withText("Total: 0.0 hours")))))
    }

    @Test
    fun shouldShowTotalInformationOnWeekendDaysIfThereIsAReport() {
        onView(allOf(hasDescendant(withText("2 Sun")), withParent(withId(R.id.reportsContainer)))).check(matches(hasDescendant(withText("Total: 3.0 hours"))))

    }

}

