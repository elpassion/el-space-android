package pl.elpassion.elspace.hub.login

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.elspace.hub.login.shortcut.ShortcutService

class LoginControllerTest {

    val api = mock<Login.HubTokenApi>()
    val view = mock<Login.View>()
    val loginRepository = mock<Login.Repository>().apply { whenever(readToken()).thenReturn(null) }
    val shortcutService = mock<ShortcutService>()
    val controller = LoginController(view, loginRepository, shortcutService, api)

    @Test
    fun shouldOpenReportListScreenIfUserIsLoggedInOnCreate() {
        whenever(loginRepository.readToken()).thenReturn("token")
        controller.onCreate()
        verify(view).openReportListScreen()
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
        verify(loginRepository).saveToken(token)
    }

    @Test
    fun shouldNotSaveGivenTokenOnLoginWhenTokenIsEmpty() {
        controller.onLogin("")
        verify(loginRepository, never()).saveToken(any())
    }

    @Test
    fun shouldShowErrorAboutEmptyTokenWhenTokenIsEmpty() {
        controller.onLogin("")
        verify(view).showEmptyLoginError()
    }

    @Test
    fun shouldNotShowErrorAboutEmptyTokenWhenTokenIsNotEmpty() {
        controller.onLogin("login")
        verify(view, never()).showEmptyLoginError()
    }

    @Test
    fun shouldOpenReportListScreenIfTokenIsNotEmptyOnLogin() {
        controller.onLogin("login")
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenIfTokenIsEmptyOnLogin() {
        controller.onLogin("")
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldCreateAppShortcutsWhenSupported() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(true)
        controller.onLogin("login")

        verify(shortcutService).creteAppShortcuts()
    }

    @Test
    fun shouldNotCreateAppShortcutsWhenDeviceNotSupported() {
        controller.onLogin("login")
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(false)

        verify(shortcutService, never()).creteAppShortcuts()
    }

    @Test
    fun shouldOpenHubWebsiteOnHub() {
        controller.onHub()
        verify(view).openHubWebsite()
    }

    @Test
    fun shouldAuthorizeInHubApiWithGoogleToken() {
        whenever(api.loginWithGoogleToken()).thenReturn(true)
        controller.onGoogleToken()
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenWhenFetchingTokenFromHubApiFailed() {
        controller.onGoogleToken()
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldShowErrorWhenFetchingTokenFromHubApiFailed() {
        controller.onGoogleToken()
        verify(view).showError()
    }

    @Test
    fun shouldNotShowErrorWhenFetchingTokenFromHubApiSucceeded() {
        whenever(api.loginWithGoogleToken()).thenReturn(true)
        controller.onGoogleToken()
        verify(view, never()).showError()
    }

    @Test
    fun shouldShowLoaderWhenFetchingTokenFromHubApi() {
        controller.onGoogleToken()
        verify(view).showLoader()
    }
}
