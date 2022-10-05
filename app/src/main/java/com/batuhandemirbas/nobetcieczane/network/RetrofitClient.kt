package com.batuhandemirbas.nobetcieczane.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    var retrofit: Retrofit? = null

    //public static final String BASE_URL = Constants.BaseUrl;
    private var BASE_URL = "https://www.nosyapi.com"

    fun retrofitInterface(context: Context?): RetrofitInterface{
        return getApiClient().create(RetrofitInterface::class.java)
    }

    fun getApiClient(): Retrofit {

        val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain: Interceptor.Chain ->
                val request = chain.request().newBuilder()
                    .header("Accept-Language", "tr")
                    .addHeader(
                        "Access-Key",
                        "I5mgwVxH9RN3aRcYmvDjNwBdiO53z4XRtzGKA5QQfg1wctSAStfpYDjAu50e" ?: ""
                    ).build()
                chain.proceed(request)
            }.build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit!!
    }
}