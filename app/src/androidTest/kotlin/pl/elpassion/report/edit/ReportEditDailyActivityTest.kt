package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
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
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.report.DailyReport
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.edit.daily.ReportEditDailyActivity
import pl.elpassion.startActivity
import rx.Completable

class ReportEditDailyActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditDailyActivity>(autoStart = false)

    private val reportEditApi = mock<ReportEdit.EditApi>()

    @Before
    fun setUp() {
        whenever(reportEditApi.editReport(any(), any(), any(), any(), any())).thenReturn(Completable.complete())
        ReportEdit.EditApiProvider.override = { reportEditApi }
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
        startActivity(newDailyReport(year = 2010, month = 2, day = 10))
        onId(R.id.reportEditDate).hasText("2010-02-10")
    }

    @Test
    fun shouldHaveSaveButton() {
        startActivity()
        onId(R.id.reportEditSaveButton).hasText(R.string.report_edit_save_button).isDisplayed()
    }

    @Test
    fun shouldCallApiWithCorrectData() {
        stubCurrentTime(2017, 1, 10)
        startActivity(newDailyReport(year = 2017, month = 1, day = 4, id = 2, reportType = DailyReportType.SICK_LEAVE))
        onId(R.id.reportEditDate).click()
        onText("OK").click()
        onId(R.id.reportEditSaveButton).click()
        verify(reportEditApi).editReport(id = 2, date = "2017-01-10", reportedHour = "0", description = "SickLeave", projectId = null)
    }

    @Test
    fun shouldHaveVisibleRemoveReportIcon() {
        startActivity()
        onId(R.id.action_remove_report).isDisplayed()
    }

    private fun startActivity(report: DailyReport = newDailyReport()) {
        rule.startActivity(ReportEditDailyActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}
