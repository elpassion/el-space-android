package pl.elpassion.elspace

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.hub.UnauthenticatedRetrofitProvider
import pl.elpassion.elspace.hub.login.GoogleSingInDI
import pl.elpassion.elspace.hub.login.GoogleHubLoginRepositoryProvider
import pl.elpassion.elspace.hub.login.GoogleHubLogin
import pl.elpassion.elspace.hub.login.GoogleHubLoginActivity
import pl.elpassion.elspace.hub.report.list.ReportListActivity

class ElSpaceApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics.Builder().core(createCrashlyticsCore()).build())
        ContextProvider.override = { applicationContext }
        setupHubLoginActivity()
    }

    private fun createCrashlyticsCore() = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()

    private fun setupHubLoginActivity() {
        GoogleHubLoginActivity.provideApi = { UnauthenticatedRetrofitProvider.get().create(GoogleHubLogin.Api::class.java) }
        GoogleHubLoginActivity.provideRepository = { GoogleHubLoginRepositoryProvider.get() }
        GoogleHubLoginActivity.openOnLoggedInScreen = { ReportListActivity.start(it) }
        GoogleHubLoginActivity.startGoogleSignInActivity = GoogleSingInDI.startGoogleSignInActivity
        GoogleHubLoginActivity.getHubGoogleSignInResult = GoogleSingInDI.getHubGoogleSignInResult
        GoogleHubLoginActivity.logoutFromGoogle = GoogleSingInDI.logoutFromGoogle
    }
}