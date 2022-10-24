package com.batuhandemirbas.nobetcieczane.data.remote

import android.content.Context
import com.batuhandemirbas.nobetcieczane.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    var retrofit: Retrofit? = null

    //public static final String BASE_URL = Constants.BaseUrl;
    private var BASE_URL = "https://www.nosyapi.com"
    private val PHARMACY_APIKEY = BuildConfig.PHARMACY_APIKEY

    fun retrofitInterface(): PharmacyService {
        return getApiClient().create(PharmacyService::class.java)
    }

    fun getApiClient(): Retrofit {

        val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain: Interceptor.Chain ->
                val request = chain.request().newBuilder()
                    .header("Accept-Language", "tr")
                    .addHeader(
                        "Access-Key",
                        PHARMACY_APIKEY
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