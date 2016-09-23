package pl.elpassion.report.list

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.isNotDisplayed
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newReport
import rx.Observable

class ReportListActivityHappyTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ReportListActivity>(ReportListActivity::class.java) {
        override fun beforeActivityLaunched() {
            stubCurrentTime(year = 2016, month = 10, day = 1)
            whenever(service.getReports()).thenReturn(Observable.just(listOf(newReport(year = 2016, month = 10, day = 1, projectName = "Project", description = "Description"))))
            ReportList.ServiceProvider.override = { service }
        }
    }

    @Test
    fun shouldNotShowErrorOnView() {
        onId(R.id.reportListError).isNotDisplayed()
    }


    @Test
    fun shouldShowDayFirstOnScreen() {
        onText("1").isDisplayed()
    }

}

