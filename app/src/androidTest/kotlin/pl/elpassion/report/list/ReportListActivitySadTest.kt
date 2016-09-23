package pl.elpassion.report.list

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onId
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.commons.apiRuntimeError

class ReportListActivitySadTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = object : ActivityTestRule<ReportListActivity>(ReportListActivity::class.java) {
        override fun beforeActivityLaunched() {
            whenever(service.getReports()).thenReturn(apiRuntimeError())
            ReportList.ServiceProvider.override = { service }
        }
    }

    @Test
    fun shouldShowErrorOnScreenWhenApiCallFails() {
        onId(R.id.reportListError).isDisplayed().hasText(R.string.report_list_error)
    }


}

