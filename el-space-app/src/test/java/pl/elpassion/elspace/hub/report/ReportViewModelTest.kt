package pl.elpassion.elspace.hub.report

import android.support.annotation.IdRes
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.project.Project
import java.lang.IllegalArgumentException

class ReportViewModelTest {

    @Test
    fun shouldReturnRegularViewModelIfActionIdIsRegularReport() {
        val reportViewModel = createReportViewModel(actionId = R.id.action_regular_report)
        assertTrue(reportViewModel is RegularViewModel)
    }

    @Test
    fun shouldReturnPaidVacationsViewModelIfActionIdIsPaidVacations() {
        val reportViewModel = createReportViewModel(actionId = R.id.action_paid_vacations_report)
        assertTrue(reportViewModel is PaidVacationsViewModel)
    }

    @Test
    fun shouldReturnDailyViewModelIfActionIdIsUnpaidVacations() {
        val reportViewModel = createReportViewModel(actionId = R.id.action_unpaid_vacations_report)
        assertTrue(reportViewModel is DailyViewModel)
    }

    @Test
    fun shouldReturnDailyViewModelIfActionIdIsSickLeave() {
        val reportViewModel = createReportViewModel(actionId = R.id.action_sick_leave_report)
        assertTrue(reportViewModel is DailyViewModel)
    }

    @Test
    fun shouldReturnDailyViewModelIfActionIdIsPaidConference() {
        val reportViewModel = createReportViewModel(actionId = R.id.action_paid_conference_report)
        assertTrue(reportViewModel is DailyViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowIllegalArgumentExceptionIfActionIdIsNotValid() {
        createReportViewModel(actionId = 123)
    }

    private fun createReportViewModel(@IdRes actionId: Int) = getReportViewModel(actionId = actionId, project = Project(11, "someProject"), date = "someDate", hours = "someHours", description = "someDescription")
}