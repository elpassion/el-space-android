package pl.elpassion.elspace.hub

import okhttp3.Interceptor
import pl.elpassion.elspace.api.createRetrofit
import pl.elpassion.elspace.api.defaultOkHttpClient
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.login.GoogleHubLoginRepositoryProvider
import retrofit2.Retrofit

object UnauthenticatedRetrofitProvider : Provider<Retrofit>({
    createRetrofit(
            okHttpClient = defaultOkHttpClient().build(),
            baseUrl = "https://hub.elpassion.com/api/v1/")
})

object HubRetrofitProvider : Provider<Retrofit>({
    createRetrofit(
            okHttpClient = defaultOkHttpClient().addInterceptor(xTokenInterceptor()).build(),
            baseUrl = "https://hub.elpassion.com/api/v1/")
})

fun xTokenInterceptor() = Interceptor { chain ->
    val request = chain.request().newBuilder()
            .addHeader("X-Access-Token", GoogleHubLoginRepositoryProvider.get().readToken())
            .build()
    chain.proceed(request)
}