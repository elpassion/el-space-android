package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.replaceText
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.common.rule
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.startActivity
import rx.Completable

class PaidVacationReportActivityTest {
    @JvmField @Rule
    val rule = rule<PaidVacationReportEditActivity>(autoStart = false)

    private val reportEditApi = mock<ReportEdit.EditApi>()
    private val reportRemoveApi = mock<ReportEdit.RemoveApi>()

    @Before
    fun setUp() {
        whenever(reportEditApi.editReport(any(), any(), any(), any(), any())).thenReturn(Completable.complete())
        whenever(reportRemoveApi.removeReport(any())).thenReturn(Completable.complete())
        ReportEdit.EditApiProvider.override = { reportEditApi }
        ReportEdit.RemoveApiProvider.override = { reportRemoveApi }
    }

    @Test
    fun shouldHaveVisibleBackArrow() {
        startActivity()
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun shouldHavePerformedAtHeader() {
        startActivity()
        onText(R.string.report_edit_date_header).isDisplayed()
    }

    @Test
    fun shouldShowCorrectReportDate() {
        startActivity(newPaidVacationHourlyReport(year = 2010, month = 2, day = 10))
        onId(R.id.reportEditDate).hasText("2010-02-10")
    }

    @Test
    fun shouldHaveCorrectHoursHeader() {
        startActivity()
        onText(R.string.report_edit_hours_header).isDisplayed()
    }

    @Test
    fun shouldHaveOldPreviousHoursValueAtTheBegging() {
        startActivity(newPaidVacationHourlyReport(reportedHours = 2.32))
        onId(R.id.reportEditHours).hasText("2.32")
    }

    @Test
    fun shouldWipeHoursOnClick() {
        startActivity()
        onId(R.id.reportEditHours).click().hasText("")
    }

    @Test
    fun shouldHaveSaveButton() {
        startActivity()
        onId(R.id.reportEditSaveButton).hasText(R.string.report_edit_save_button).isDisplayed()
    }

    @Test
    fun shouldCallApiWithCorrectData() {
        startActivity(newPaidVacationHourlyReport(reportedHours = 2.0, year = 2010, month = 10, day = 1, id = 2))
        insertData(reportedHours = "5.5")
        verify(reportEditApi).editReport(id = 2, date = "2010-10-01", reportedHour = "5.5", description = "", projectId = null)
    }

    @Test
    fun shouldHaveVisibleRemoveReportIcon() {
        startActivity()
        onId(R.id.action_remove_report).isDisplayed()
    }

    @Test
    fun shouldCallRemoveReportApiAfterClickOnRemove() {
        startActivity(newPaidVacationHourlyReport(id = 2))
        onId(R.id.action_remove_report).click()
        verify(reportRemoveApi).removeReport(reportId = 2)
    }

    private fun insertData(reportedHours: String) {
        onId(R.id.reportEditHours).perform(replaceText(reportedHours), closeSoftKeyboard())
        onId(R.id.reportEditSaveButton).click()
    }

    private fun startActivity(report: PaidVacationHourlyReport = newPaidVacationHourlyReport()) {
        rule.startActivity(PaidVacationReportEditActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}