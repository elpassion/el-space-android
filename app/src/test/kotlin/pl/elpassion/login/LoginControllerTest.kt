package pl.elpassion.login

import com.nhaarman.mockito_kotlin.*
import org.junit.Test

class LoginControllerTest {

    @Test
    fun shouldOpenReportListScreenIfUserIsLoggedInOnCreate() {
        val view = mock<Login.View>()
        val loginRepository = mock<Login.Repository>().apply { whenever(isLoggedIn()).thenReturn(true) }
        LoginController(view, loginRepository).onCreate()
        verify(view, times(1)).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenIfUserIsNotLoggedInOnCreate() {
        val view = mock<Login.View>()
        val loginRepository = mock<Login.Repository>().apply { whenever(isLoggedIn()).thenReturn(false) }
        LoginController(view, loginRepository).onCreate()
        verify(view, never()).openReportListScreen()
    }

}

interface Login {
    interface View {
        fun openReportListScreen()
    }

    interface Repository {
        fun isLoggedIn() : Boolean
    }
}

class LoginController(val view: Login.View, val loginRepository: Login.Repository) {
    fun onCreate() {
        if(loginRepository.isLoggedIn()){
            view.openReportListScreen()
        }
    }

}
