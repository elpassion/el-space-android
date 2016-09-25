package pl.elpassion.login

import com.nhaarman.mockito_kotlin.*
import org.junit.Test

class LoginControllerTest {

    val view = mock<Login.View>()
    val loginRepository = mock<Login.Repository>().apply { whenever(readToken()).thenReturn(null) }
    val controller = LoginController(view, loginRepository)

    @Test
    fun shouldOpenReportListScreenIfUserIsLoggedInOnCreate() {
        whenever(loginRepository.readToken()).thenReturn("token")
        controller.onCreate()
        verify(view, times(1)).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenIfUserIsNotLoggedInOnCreate() {
        controller.onCreate()
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldSaveGivenTokenOnLogin() {
        val token = "token"
        controller.onLogin(token)
        verify(loginRepository, times(1)).saveToken(token)
    }

    @Test
    fun shouldNotSaveGivenTokenOnLoginWhenTokenIsEmpty() {
        controller.onLogin("")
        verify(loginRepository, never()).saveToken(any())
    }

    @Test
    fun shouldShowErrorAboutEmptyTokenWhenTokenIsEmpty() {
        controller.onLogin("")
        verify(view, times(1)).showEmptyLoginError()
    }

    @Test
    fun shouldNotShowErrorAboutEmptyTokenWhenTokenIsNotEmpty() {
        controller.onLogin("login")
        verify(view, never()).showEmptyLoginError()
    }

    @Test
    fun shouldOpenReportListScreenIfTokenIsNotEmptyOnLogin() {
        controller.onLogin("login")
        verify(view, times(1)).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenIfTokenIsEmptyOnLogin() {
        controller.onLogin("")
        verify(view, never()).openReportListScreen()
    }

}

interface Login {
    interface View {
        fun openReportListScreen()
        fun showEmptyLoginError()
    }

    interface Repository {
        fun readToken(): String?
        fun saveToken(token: String)
    }
}

class LoginController(val view: Login.View, val loginRepository: Login.Repository) {
    fun onCreate() {
        if (loginRepository.readToken() != null) {
            view.openReportListScreen()
        }
    }

    fun onLogin(token: String) {
        if (token.isNotEmpty()) {
            loginRepository.saveToken(token)
            view.openReportListScreen()
        } else {
            view.showEmptyLoginError()
        }
    }

}
