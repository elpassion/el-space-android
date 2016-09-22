package pl.elpassion.activities

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class ActivitiesControllerTest {

    @Test
    fun shouldDisplayActivitiesReturnedFromApiOnCreate() {
        val api = mock<Activities.Api>()
        val reports = listOf(Activity())
        whenever(api.getActivities()).thenReturn(reports)
        val view = mock<Activities.View>()
        val controller = ActivitiesController(api, view)
        controller.onCreate()
        verify(view, times(1)).showActivities(reports)
    }

}

interface Activities {

    interface Api {
        fun getActivities(): List<Activity>
    }

    interface View {
        fun showActivities(reports: List<Activity>)
    }

}

class Activity()

class ActivitiesController(val api: Activities.Api, val view: Activities.View) {
    fun onCreate() {
        view.showActivities(api.getActivities())
    }
}
