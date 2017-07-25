package pl.elpassion.elspace.hub.report.list

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportAddActivity
import io.reactivex.Observable

class ReportListActivityScrollToTodayTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = rule<ReportListActivity>(autoStart = false)

    @Test
    fun shouldScrollToTodayOnTodayClickWhenNoReports() {
        stubServiceAndStart(reports = emptyList(), year = 2017, month = 1, day = 31)
        onId(R.id.action_today).click()
        Thread.sleep(100)
        onId(R.id.reportsContainer).hasChildWithText("31 Tue")
    }

    @Test
    fun shouldReallyScrollToTodayOnTodayClickWhenNoReports() {
        stubServiceAndStart(reports = emptyList(), year = 2017, month = 1, day = 1)
        onId(R.id.action_today).click()
        Thread.sleep(100)
        onId(R.id.reportsContainer).hasChildWithText("1 Sun")
        onId(R.id.reportsContainer).doesNotHaveChildWithText("31 Tue")
    }

    @Test
    fun shouldScrollToTodayOnTodayClickWhenOneReportIsAdded() {
        stubServiceAndStart(
                reports = listOf(newRegularHourlyReport(year = 2017, month = 1, day = 1, reportedHours = 3.0)),
                year = 2017, month = 1, day = 31)
        onId(R.id.action_today).click()
        Thread.sleep(100)
        onId(R.id.reportsContainer).hasChildWithText("31 Tue")
    }


    private fun stubServiceAndStart(reports: List<Report>, year: Int = 2016, month: Int = 6, day: Int = 1) {
        val date = getDateString(year, month, day)
        stubCurrentTime(date)
        whenever(service.getReports(any())).thenReturn(Observable.just(reports))
        ReportList.ServiceProvider.override = { service }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), date))
    }
}