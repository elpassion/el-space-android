package pl.elpassion.activities

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ActivitiesControllerTest {

    @Test
    fun shouldCallApiForActivitiesOnCreate() {
        val api = mock<Activities.Api>()
        val controller = ActivitiesController(api)
        controller.onCreate()
        verify(api, times(1)).getActivities()
    }

}

interface Activities {

    interface Api {
        fun getActivities()
    }

}

class ActivitiesController(val api: Activities.Api) {
    fun onCreate() {
        api.getActivities()
    }

}
