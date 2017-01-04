package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.isDisplayed
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.common.rule
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.edit.daily.ReportEditDailyActivity
import pl.elpassion.report.edit.paidvacation.ReportEditPaidVacationActivity
import pl.elpassion.startActivity

class ReportEditDailyActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditDailyActivity>(autoStart = false)

    @Test
    fun shouldHaveVisibleBackArrow() {
        startActivity()
        onToolbarBackArrow().isDisplayed()
    }

    private fun startActivity(report: PaidVacationHourlyReport = newPaidVacationHourlyReport()) {
        rule.startActivity(ReportEditPaidVacationActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}
