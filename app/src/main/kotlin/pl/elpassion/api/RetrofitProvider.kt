package pl.elpassion.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.elpassion.common.Provider
import pl.elpassion.login.LoginRepositoryProvider
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitProvider : Provider<Retrofit>({
    Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
            .baseUrl("https://hub.elpassion.com/api/v1/")
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).addInterceptor(xTokenInterceptor()).build())
            .build()
})


fun xTokenInterceptor() = Interceptor { chain ->
    val request = chain.request().newBuilder()
            .addHeader("X-Access-Token", LoginRepositoryProvider.get().readToken())
            .build()
    chain.proceed(request)
}