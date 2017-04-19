package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class DebateDetailsControllerTest {

    @Test
    fun shouldCallApiOnCreate() {
        val api = mock<DebateDetails.Api>()
        DebateDetailsController(api).onCreate()
        verify(api).getDebateDetails()
    }
}

interface DebateDetails {
    interface Api {
        fun getDebateDetails()
    }
}

class DebateDetailsController(private val api: DebateDetails.Api) {
    fun onCreate() {
        api.getDebateDetails()
    }
}
