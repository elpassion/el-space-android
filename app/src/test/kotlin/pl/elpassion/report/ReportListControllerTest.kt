package pl.elpassion.report

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class ReportListControllerTest {

    @Test
    fun shouldDisplayReportsReturnedFromApiOnCreate() {
        val api = mock<ReportList.Api>()
        val reports = listOf(Report())
        whenever(api.getActivities()).thenReturn(reports)
        val view = mock<ReportList.View>()
        val controller = ActivitiesController(api, view)
        controller.onCreate()
        verify(view, times(1)).showActivities(reports)
    }

}

interface ReportList {

    interface Api {
        fun getActivities(): List<Report>
    }

    interface View {
        fun showActivities(reports: List<Report>)
    }

}

class Report()

class ActivitiesController(val api: ReportList.Api, val view: ReportList.View) {
    fun onCreate() {
        view.showActivities(api.getActivities())
    }
}
