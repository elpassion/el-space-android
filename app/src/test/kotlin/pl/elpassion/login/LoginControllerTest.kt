package pl.elpassion.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class LoginControllerTest {

    @Test
    fun shouldOpenReportListScreenIfUserIsLoggedInOnCreate() {
        val view = mock<Login.View>()
        LoginController(view).onCreate()
        verify(view, times(1)).openReportListScreen()
    }

}

interface Login {
    interface View {
        fun openReportListScreen()
    }
}

class LoginController(val view: Login.View) {
    fun onCreate() {
        view.openReportListScreen()
    }

}
