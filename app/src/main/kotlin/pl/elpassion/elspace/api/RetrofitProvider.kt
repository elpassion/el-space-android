package pl.elpassion.elspace.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.login.LoginRepositoryProvider
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object UnauthenticatedRetrofitProvider : Provider<Retrofit>({
    createRetrofit(defaultOkHttpClient().build())
})

object RetrofitProvider : Provider<Retrofit>({
    createRetrofit(defaultOkHttpClient().addInterceptor(xTokenInterceptor()).build())
})

private fun createRetrofit(okHttpClient: OkHttpClient?): Retrofit {
    return Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
            .baseUrl("https://hub.elpassion.com/api/v1/")
            .client(okHttpClient)
            .build()
}

private fun defaultOkHttpClient() = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

fun xTokenInterceptor() = Interceptor { chain ->
    val request = chain.request().newBuilder()
            .addHeader("X-Access-Token", LoginRepositoryProvider.get().readToken())
            .build()
    chain.proceed(request)
}