package pl.elpassion.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.elpassion.common.Provider
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
            .addHeader("X-Access-Token", "1d3f080c9796ced032bb60410f40f47c")
            .build()
    chain.proceed(request)
}