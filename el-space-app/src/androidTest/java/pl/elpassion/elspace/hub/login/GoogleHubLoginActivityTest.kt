package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.elpassion.android.commons.espresso.*
import com.google.android.gms.common.api.GoogleApiClient
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.stubbing.OngoingStubbing
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.getAutoFinishingIntent
import pl.elpassion.elspace.common.prepareAutoFinishingIntent
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.hub.login.GoogleHubLogin.*

class GoogleHubLoginActivityTest {

    val api = mock<Api>()
    val repository = mock<Repository>()
    val openOnLoggedInScreen = mock<(Context) -> Unit>()
    val startGoogleSignInActivity = mock<(Activity, () -> GoogleApiClient, Int) -> Unit>()
    val getHubGoogleSignInResult = mock<(Intent?) -> HubGoogleSignInResult>()
    val logoutFromGoogle = mock<(() -> GoogleApiClient) -> Unit>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @Rule @JvmField
    val rule = rule<GoogleHubLoginActivity>(autoStart = false) {
        GoogleHubLoginActivity.provideApi = { api }
        GoogleHubLoginActivity.provideRepository = { repository }
        GoogleHubLoginActivity.openOnLoggedInScreen = openOnLoggedInScreen
        GoogleHubLoginActivity.startGoogleSignInActivity = startGoogleSignInActivity
        GoogleHubLoginActivity.getHubGoogleSignInResult = getHubGoogleSignInResult
        GoogleHubLoginActivity.logoutFromGoogle = logoutFromGoogle
    }

    @Before
    fun setUp() {
        prepareAutoFinishingIntent()
        whenever(startGoogleSignInActivity.invoke(any(), any(), any())).thenAnswer {
            it.getArgument<Activity>(0).startActivityForResult(getAutoFinishingIntent(), it.getArgument<Int>(2))
        }
        whenever(api.loginWithGoogle(any())).thenJust(HubTokenFromApi("token"))
    }

    @Test
    fun shouldShowLoaderWhileApiCall() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult()
        whenever(api.loginWithGoogle(any())).thenNever()
        rule.startActivity()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldOpenOnLoggedInScreenIfUserIsLoggedInOnCreate() {
        whenever(repository.readToken()).thenReturn("token")
        rule.startActivity()
        verify(openOnLoggedInScreen).invoke(any())
    }

    @Test
    fun shouldCloseLoginActivityIfUserIsLoggedInOnCreate() {
        whenever(repository.readToken()).thenReturn("token")
        rule.startActivity()
        Assert.assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldStartGoogleLoginIntentOnCreate() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult()
        rule.startActivity()
        verify(startGoogleSignInActivity).invoke(any(), any(), any())
    }

    @Test
    fun shouldGetHubGoogleSignInResultFromIntentOnActivityResult() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult()
        rule.startActivity()
        verify(getHubGoogleSignInResult).invoke(anyOrNull())
    }

    @Test
    fun shouldCallApiWithGoogleTokenFromGetHubGoogleSignInResult() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult()
        rule.startActivity()
        verify(api).loginWithGoogle(GoogleTokenForHubTokenApi("googleToken"))
    }

    @Test
    fun shouldLogoutFromGoogleWhenApiCallFail() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult()
        whenever(api.loginWithGoogle(any())).thenError()
        rule.startActivity()
        verify(logoutFromGoogle).invoke(any())
    }

    @Test
    fun shouldShowGoogleLoginError() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult(isSuccess = false)
        rule.startActivity()
        onId(R.id.loader).doesNotExist()
        onText(R.string.google_login_error).isDisplayed()
    }

    @Test
    fun shouldShowTokenLoginError() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult()
        whenever(api.loginWithGoogle(any())).thenError()
        rule.startActivity()
        onId(R.id.loader).doesNotExist()
        onText(R.string.google_token_error).isDisplayed()
    }

    @Test
    fun shouldShowDismissErrorButtonOnGoogleLoginError() {
        whenever(repository.readToken()).thenReturn(null)
        stubGetHubGoogleSignInResult(isSuccess = false)
        rule.startActivity()
        onId(R.id.googleDismissErrorButton).isDisplayed()
    }

    private fun stubGetHubGoogleSignInResult(isSuccess: Boolean = true) {
        whenever(getHubGoogleSignInResult.invoke(anyOrNull())).thenReturn(HubGoogleSignInResult(isSuccess = isSuccess, googleToken = "googleToken"))
    }

    private fun <T> OngoingStubbing<Single<T>>.thenJust(value: T) {
        thenReturn(Single.just(value))
    }

    private fun <T> OngoingStubbing<Single<T>>.thenError() {
        thenReturn(Single.error(RuntimeException()))
    }

    private fun <T> OngoingStubbing<Single<T>>.thenNever() {
        thenReturn(Single.never())
    }
}
