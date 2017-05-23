package pl.elpassion.elspace.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.elpassion.elspace.common.Provider
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val rxJava2CallAdapterFactory by lazy { RxJava2CallAdapterFactory.create() }
private val gsonConverter by lazy {
    GsonConverterFactory.create(
            GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    )
}

private val httpLoggingInterceptor by lazy {
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
}


object DebateRetrofitProvider : Provider<Retrofit>({
    createRetrofit(
            okHttpClient = defaultOkHttpClient().build(),
            baseUrl = "http://el-debate.herokuapp.com/api/")
})

private fun createRetrofit(okHttpClient: OkHttpClient?, baseUrl: String): Retrofit {
    return Retrofit.Builder()
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .addConverterFactory(gsonConverter)
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
}

private fun defaultOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
