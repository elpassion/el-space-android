package pl.elpassion.elspace.hub.login.instant

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.elpassion.android.commons.espresso.InitIntentsRule
import com.google.android.gms.common.api.GoogleApiClient
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.stubbing.OngoingStubbing
import pl.elpassion.elspace.common.getAutoFinishingIntent
import pl.elpassion.elspace.common.prepareAutoFinishingIntent
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.hub.login.GoogleTokenForHubTokenApi
import pl.elpassion.elspace.hub.login.HubTokenFromApi

class InstantGoogleHubLoginActivityTest {

    val api = mock<InstantGoogleHubLogin.Api>()
    val repository = mock<InstantGoogleHubLogin.Repository>()
    val openOnLoggedInScreen = mock<(Context) -> Unit>()
    val startGoogleSignInActivity = mock<(Activity, () -> GoogleApiClient, Int) -> Unit>()
    val getHubGoogleSignInResult = mock<(Intent?) -> InstantGoogleHubLogin.HubGoogleSignInResult>()
    val logoutFromGoogle = mock<(() -> GoogleApiClient) -> Unit>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @Rule @JvmField
    val rule = rule<InstantGoogleHubLoginActivity>(autoStart = false) {
        InstantGoogleHubLoginActivity.provideApi = { api }
        InstantGoogleHubLoginActivity.provideRepository = { repository }
        InstantGoogleHubLoginActivity.openOnLoggedInScreen = openOnLoggedInScreen
        InstantGoogleHubLoginActivity.startGoogleSignInActivity = startGoogleSignInActivity
        InstantGoogleHubLoginActivity.getHubGoogleSignInResult = getHubGoogleSignInResult
        InstantGoogleHubLoginActivity.logoutFromGoogle = logoutFromGoogle
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
    fun shouldOpenOnLoggedInScreenIfUserIsLoggedInOnCreate() {
        whenever(repository.readToken()).thenReturn("token")
        rule.startActivity()
        verify(openOnLoggedInScreen).invoke(any())
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

    private fun stubGetHubGoogleSignInResult() {
        whenever(getHubGoogleSignInResult.invoke(anyOrNull())).thenReturn(InstantGoogleHubLogin.HubGoogleSignInResult(isSuccess = true, googleToken = "googleToken"))
    }

    private fun <T> OngoingStubbing<Single<T>>.thenJust(value: T) {
        thenReturn(Single.just(value))
    }

    private fun <T> OngoingStubbing<Single<T>>.thenError() {
        thenReturn(Single.error(RuntimeException()))
    }
}
