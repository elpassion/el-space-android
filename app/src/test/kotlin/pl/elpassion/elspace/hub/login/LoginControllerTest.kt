package pl.elpassion.elspace.hub.login

import com.elpassion.android.commons.rxjavatest.thenError
import com.elpassion.android.commons.rxjavatest.thenJust
import com.elpassion.android.commons.rxjavatest.thenNever
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
    fun shouldCreateAppShortcutsWhenLoggedWithGoogle() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(true)
        stubHubApiToReturnToken()
        controller.onGoogleToken()

        verify(shortcutService).creteAppShortcuts()
    }

    @Test
    fun shouldNotCreateAppShortcutsWhenDeviceNotSupported() {
        whenever(shortcutService.isSupportingShortcuts()).thenReturn(false)
        controller.onLogin("login")

        verify(shortcutService, never()).creteAppShortcuts()
    }

    @Test
    fun shouldOpenHubWebsiteOnHub() {
        controller.onHub()
        verify(view).openHubWebsite()
    }

    @Test
    fun shouldAuthorizeInHubApiWithGoogleToken() {
        stubHubApiToReturnToken()
        controller.onGoogleToken()
        verify(view).openReportListScreen()
    }

    @Test
    fun shouldNotOpenReportListScreenWhenFetchingTokenFromHubApiFailed() {
        stubHubApiToReturnError()
        controller.onGoogleToken()
        verify(view, never()).openReportListScreen()
    }

    @Test
    fun shouldShowErrorWhenFetchingTokenFromHubApiFailed() {
        stubHubApiToReturnError()
        controller.onGoogleToken()
        verify(view).showError()
    }

    @Test
    fun shouldNotShowErrorWhenFetchingTokenFromHubApiSucceeded() {
        stubHubApiToReturnToken()
        controller.onGoogleToken()
        verify(view, never()).showError()
    }

    @Test
    fun shouldShowLoaderWhenFetchingTokenFromHubApi() {
        stubHubApiToNeverReturn()
        controller.onGoogleToken()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderAfterFetchingToken() {
        stubHubApiToReturnToken()
        controller.onGoogleToken()
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderUntilFetchingTokenFinished() {
        stubHubApiToNeverReturn()
        controller.onGoogleToken()
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldSaveTokenWhenFetchingTokenFromHubApiSucceeded() {
        stubHubApiToReturnToken()
        controller.onGoogleToken()
        verify(loginRepository).saveToken("token")
    }

    private fun stubHubApiToReturnToken() {
        whenever(api.loginWithGoogleToken()).thenJust("token")
    }

    private fun stubHubApiToNeverReturn() {
        whenever(api.loginWithGoogleToken()).thenNever()
    }

    private fun stubHubApiToReturnError() {
        whenever(api.loginWithGoogleToken()).thenError(RuntimeException())
    }
}
