package pl.elpassion.report.list

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.project.CachedProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.report.add.ReportAdd
import rx.Completable
import rx.Observable

class ReportListActivityAddReportTest {

    val service = mock<ReportList.Service>()
    val addReportService = mock<ReportAdd.Api>().apply { whenever(addRegularReport(any(), any(), any(), any())).thenReturn(Completable.complete()) }

    @JvmField @Rule
    val rule = rule<ReportListActivity> {
        ReportAdd.ApiProvider.override = { addReportService }
        CachedProjectRepositoryProvider.override = { stubProjectRepository() }
        stubCurrentTime(year = 2016, month = 10, day = 1)
        whenever(service.getReports())
                .thenReturn(Observable.just(emptyList()))
                .thenReturn(Observable.just(listOf(newRegularHourlyReport(year = 2016, month = 10, day = 1, project = newProject(name = "Project"), description = "Description", reportedHours = 8.0))))
        ReportList.ServiceProvider.override = { service }
    }

    @Test
    fun shouldCloseAddReportActivityAndMakeSecondCallToUpdateReports() {
        onId(R.id.reportsContainer).check(matches(hasDescendant(not(withText("Description")))))
        onText("1 Sat").click()
        onId(R.id.reportAddDescription).perform(ViewActions.replaceText("Description"))
        Espresso.closeSoftKeyboard()
        onId(R.id.addReport).click()
        onId(R.id.reportsContainer).check(matches(hasDescendant(withText("Description"))))
    }

    private fun stubProjectRepository() = mock<CachedProjectRepository>().apply {
        whenever(hasProjects()).thenReturn(true)
        whenever(getPossibleProjects()).thenReturn(listOf(newProject()))
    }
}

