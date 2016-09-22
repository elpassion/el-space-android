package pl.elpassion.activities

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ActivitiesControllerTest {

    @Test
    fun shouldDisplayActivitiesReturnedFromApiOnCreate() {
        val api = mock<Activities.Api>()
        val view = mock<Activities.View>()
        val controller = ActivitiesController(api, view)
        controller.onCreate()
        verify(view, times(1)).showActivities()
    }

}

interface Activities {

    interface Api {
        fun getActivities()
    }

    interface View {
        fun showActivities()
    }

}

class ActivitiesController(val api: Activities.Api, val view: Activities.View) {
    fun onCreate() {
        api.getActivities()
        view.showActivities()
    }
}
