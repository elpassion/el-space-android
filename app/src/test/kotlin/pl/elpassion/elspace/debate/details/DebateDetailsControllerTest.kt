package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class DebateDetailsControllerTest {

    @Test
    fun shouldCallApiWithGivenTokenOnCreate() {
        val api = mock<DebateDetails.Api>()
        DebateDetailsController(api).onCreate(token = "token")
        verify(api).getDebateDetails(token = "token")
    }

    @Test
    fun shouldReallyCallApiWithGivenTokenOnCreate() {
        val api = mock<DebateDetails.Api>()
        DebateDetailsController(api).onCreate(token = "otherToken")
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
