package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class DebateDetailsControllerTest {

    private val api = mock<DebateDetails.Api>()
    private val controller = DebateDetailsController(api)

    @Test
    fun shouldCallApiWithGivenTokenOnCreate() {
        controller.onCreate(token = "token")
        verify(api).getDebateDetails(token = "token")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenOnCreate() {
        controller.onCreate(token = "otherToken")
        verify(api).getDebateDetails(token = "otherToken")
    }

}

interface DebateDetails {
    interface Api {
        fun getDebateDetails(token: String)
    }
}

class DebateDetailsController(private val api: DebateDetails.Api) {
    fun onCreate(token: String) {
        api.getDebateDetails(token)
    }
}
