package pl.elpassion.report.list

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
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
import pl.elpassion.common.rule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.project.CachedProjectRepositoryProvider
import pl.elpassion.project.dto.newHoursReport
import pl.elpassion.project.dto.newProject
import pl.elpassion.report.add.ReportAddActivity
import rx.Observable

class ReportListActivityTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = rule<ReportListActivity> {
        CachedProjectRepositoryProvider.override = { mock<CachedProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
        stubCurrentTime(year = 2016, month = 10, day = 4)
        whenever(service.getReports()).thenReturn(Observable.just(listOf(
                newHoursReport(year = 2016, month = 10, day = 3, project = newProject(name = "Project"), description = "Description", reportedHours = 8.0),
                newHoursReport(year = 2016, month = 10, day = 2, reportedHours = 3.0),
                newHoursReport(year = 2016, month = 10, day = 6, reportedHours = 4.0))))
        ReportList.ServiceProvider.override = { service }
    }

    @JvmField @Rule
    val intents = InitIntentsRule()

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
    fun shouldOpenAddReportScreenOnWeekendDayClick() {
        onText("1 Sat").click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldOpenAddReportScreenOnNotFilledInDayClick() {
        onText("4 Tue").click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldOpenAddReportScreenOnNormalDayClick() {
        onText("3 Mon").click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldHaveOneDayWithMissingStatus() {
        onId(R.id.reportsContainer).hasChildWithText(R.string.report_missing)
    }

    @Test
    fun shouldNotHaveMissingInformationOnWeekendDays() {
        verifyIfDayNumberOneHasNotMissingText()
    }

    @Test
    fun shouldNotHaveTotalInformationOnNotPassedDays() {
        verifyIfFifthDayHasNoInformationAboutTotal()
    }

    @Test
    fun shouldShowTotalInformationOnWeekendDaysIfThereIsAReport() {
        verifyIfWeekendDayWithReportHasTotalInformation()
    }

    @Test
    fun shouldShowTotalInformationOnDayFromFutureIfThereAreReports() {
        onView(withId(R.id.reportsContainer)).perform(scrollToPosition<RecyclerView.ViewHolder>(6))
        verifyIfDayFromFutureWithReportsHasTotalInformation()
    }

    @Test
    fun shouldShowCorrectlyMonthNameOnStart() {
        onId(R.id.monthTitle)
                .isDisplayed()
                .hasText("October")
    }

    @Test
    fun shouldShowCorrectlyMonthNameAfterClickOnNextMonth() {
        onId(R.id.nextMonthButton).click()

        onId(R.id.monthTitle)
                .isDisplayed()
                .hasText("November")
    }

    @Test
    fun shouldShowCorrectlyMonthNameAfterClickOnPrevMonth() {
        onId(R.id.prevMonthButton).click()

        onId(R.id.monthTitle)
                .isDisplayed()
                .hasText("September")
    }

    @Test
    fun shouldOpenAddReportOnClickOnFAB() {
        onId(R.id.fabAddReport).click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldDisplayCorrectDescriptionOnNextButton() {
        onId(R.id.nextMonthButton).check(matches(withContentDescription(R.string.next_month)))
    }

    @Test
    fun shouldDisplayCorrectDescriptionOnPreviousButton() {
        onId(R.id.prevMonthButton).check(matches(withContentDescription(R.string.previous_month)))
    }

    private fun verifyIfDayNumberOneHasNotMissingText() {
        onView(allOf(hasDescendant(withText("1 Sat")), withParent(withId(R.id.reportsContainer)))).check(matches(not(hasDescendant(withText(R.string.report_missing)))))
    }

    private fun verifyIfFifthDayHasNoInformationAboutTotal() {
        onView(allOf(hasDescendant(withText("5 Wed")), withParent(withId(R.id.reportsContainer)))).check(matches(not(hasDescendant(withText("Total: 0.0 hours")))))
    }

    private fun verifyIfWeekendDayWithReportHasTotalInformation() {
        onView(allOf(hasDescendant(withText("2 Sun")), withParent(withId(R.id.reportsContainer)))).check(matches(hasDescendant(withText("Total: 3.0 hours"))))
    }

    private fun verifyIfDayFromFutureWithReportsHasTotalInformation() {
        onView(allOf(hasDescendant(withText("6 Thu")), withParent(withId(R.id.reportsContainer)))).check(matches(hasDescendant(withText("Total: 4.0 hours"))))
    }

}

