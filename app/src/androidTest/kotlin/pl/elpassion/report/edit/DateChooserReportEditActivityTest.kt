package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.common.rule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.edit.regular.ReportEditRegularActivity
import pl.elpassion.startActivity

class DateChooserReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditRegularActivity>(autoStart = false)

    @Before
    fun setUp() {
        stubCurrentTime(2013, 1, 3)
    }

    @Test
    fun shouldSetTodayDateAfterDateChange() {
        startActivity()
        CurrentTimeProvider.override = { getTimeFrom(2015, 11, 20).timeInMillis }
        onId(R.id.reportEditDate).click()
        onText("OK").click()
        onId(R.id.reportEditDate).hasText("2015-12-20")
    }


    private fun startActivity(report: RegularHourlyReport = newRegularHourlyReport()) {
        rule.startActivity(ReportEditRegularActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}

