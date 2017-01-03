package pl.elpassion.report.list

import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.Matchers.instanceOf
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
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.Report
import pl.elpassion.report.add.ReportAddActivity
import pl.elpassion.report.edit.paidvacation.ReportEditPaidVacationActivity
import pl.elpassion.report.edit.regular.ReportEditRegularActivity
import rx.Observable

class ReportListActivityTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = rule<ReportListActivity> {
        CachedProjectRepositoryProvider.override = { mock<CachedProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
        stubCurrentTime(year = 2016, month = 10, day = 4)
        whenever(service.getReports()).thenReturn(Observable.just(listOf<Report>(
                newRegularHourlyReport(year = 2016, month = 10, day = 3, project = newProject(name = "Project"), description = "Description", reportedHours = 8.0),
                newRegularHourlyReport(year = 2016, month = 10, day = 2, reportedHours = 3.0),
                newRegularHourlyReport(year = 2016, month = 10, day = 6, reportedHours = 4.0),
                newDailyReport(year = 2016, month = 10, day = 7, reportType = DailyReportType.SICK_LEAVE),
                newDailyReport(year = 2016, month = 10, day = 8, reportType = DailyReportType.UNPAID_VACATIONS),
                newRegularHourlyReport(year = 2016, month = 10, day = 9, reportedHours = 3.0),
                newPaidVacationHourlyReport(year = 2016, month = 10, day = 10, reportedHours = 3.0))))
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
    fun shouldShowRegularHourlyReportOnContainer() {
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
        onToolbarTitle()
                .isDisplayed()
                .hasText("October")
    }

    @Test
    fun shouldShowCorrectlyMonthNameAfterClickOnNextMonth() {
        onId(R.id.action_next_month).click()

        onToolbarTitle()
                .isDisplayed()
                .hasText("November")
    }

    @Test
    fun shouldShowCorrectlyMonthNameAfterClickOnPrevMonth() {
        onId(R.id.action_prev_month).click()

        onToolbarTitle()
                .isDisplayed()
                .hasText("September")
    }

    @Test
    fun shouldOpenAddReportOnClickOnFAB() {
        onId(R.id.fabAddReport).click()

        checkIntent(ReportAddActivity::class.java)
    }

    @Test
    fun shouldNotShowMissingInformationWhenDayHasNoReportsButIsFromFuture() {
        scrollToItemWithText("9 Sun")
        onItemWithText("9 Sun").check(matches(not(hasDescendant(withText(R.string.report_missing)))))
    }

    @Test
    fun shouldDisplayCorrectDescriptionOnNextButton() {
        onId(R.id.action_next_month).check(matches(withContentDescription(R.string.next_month)))
    }

    @Test
    fun shouldDisplayCorrectDescriptionOnPreviousButton() {
        onId(R.id.action_prev_month).check(matches(withContentDescription(R.string.previous_month)))
    }

    @Test
    fun shouldShowSickLeaveInformationForDailyReportTypeSickLeave() {
        onItemWithText("7 Fri").check(matches(hasDescendant(withText(R.string.report_sick_leave_title))))
    }

    @Test
    fun shouldShowUnpaidVacationsInformationForDailyReportTypeUnpaidVacations() {
        onItemWithText("8 Sat").check(matches(hasDescendant(withText(R.string.report_unpaid_vacations_title))))
    }

    @Test
    fun shouldShowPaidVacationsInformationForPaidVacationReport() {
        scrollToItemWithText("11 Tue")
        onId(R.id.reportsContainer).hasChildWithText("3.0h - ${getTargetContext().getString(R.string.report_paid_vacations_title)}")
    }

    @Test
    fun shouldOpenPaidVacationReportEditActivityAfterClickOnPaidVacationReport() {
        scrollToItemWithText("11 Tue")
        onText("3.0h - ${getTargetContext().getString(R.string.report_paid_vacations_title)}").click()

        checkIntent(ReportEditPaidVacationActivity::class.java)
    }

    @Test
    fun shouldOpenRegularReportEditActivityAfterClickOnRegularReport() {
        onText("8.0h - Project").click()

        checkIntent(ReportEditRegularActivity::class.java)
    }

    private fun verifyIfDayNumberOneHasNotMissingText() {
        onItemWithText("1 Sat").check(matches(not(hasDescendant(withText(R.string.report_missing)))))
    }

    private fun verifyIfFifthDayHasNoInformationAboutTotal() {
        onItemWithText("5 Wed").check(matches(not(hasDescendant(withText("Total: 0.0 hours")))))
    }

    private fun verifyIfWeekendDayWithReportHasTotalInformation() {
        onItemWithText("2 Sun").check(matches(hasDescendant(withText("Total: 3.0 hours"))))
    }

    private fun verifyIfDayFromFutureWithReportsHasTotalInformation() {
        onItemWithText("6 Thu").check(matches(hasDescendant(withText("Total: 4.0 hours"))))
    }

    private fun onItemWithText(text: String) = onView(allOf(hasDescendant(withText(text)), withParent(withId(R.id.reportsContainer))))

    private fun onToolbarTitle() = onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.toolbar))))

    private fun scrollToItemWithText(s: String) {
        onView(withId(R.id.reportsContainer)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(s))))
    }
}

