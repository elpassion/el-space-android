package pl.elpassion.elspace.hub.report.list

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import com.elpassion.android.commons.espresso.InitIntentsRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.CachedProjectRepository
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport

class ReportListActivityPluralsTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = rule<ReportListActivity> {
        CachedProjectRepositoryProvider.override = {
            mock<CachedProjectRepository>().apply {
                whenever(getPossibleProjects()).thenReturn(listOf(newProject()))
            }
        }
        stubCurrentTime(year = 2016, month = 10, day = 5)
        whenever(service.getReports(any())).thenReturn(Observable.just(listOf(
                newRegularHourlyReport(year = 2016, month = 10, day = 3,
                        project = newProject(name = "Project"), description = "Description",
                        reportedHours = 8.0),
                newRegularHourlyReport(year = 2016, month = 10, day = 4,
                        project = newProject(name = "Project"), description = "Description",
                        reportedHours = 1.0))))
        ReportList.ServiceProvider.override = { service }
    }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @Test
    fun shouldShowTotalHoursCorrectlyForQuantitiesMoreThanOne() {
        onItemWithText("3 Mon").check(matches(hasDescendant(withText("Total: 8 hours"))))
    }

    @Test
    fun shouldShowTotalHoursCorrectlyForQuantityOne() {
        onItemWithText("4 Tue").check(matches(hasDescendant(withText("Total: 1 hour"))))
    }

    private fun onItemWithText(text: String) = onView(
            allOf(hasDescendant(withText(text)), withParent(withId(R.id.reportsContainer))))
}

