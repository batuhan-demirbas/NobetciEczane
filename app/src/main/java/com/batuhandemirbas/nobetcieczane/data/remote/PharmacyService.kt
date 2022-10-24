package com.batuhandemirbas.nobetcieczane.data.remote

import com.batuhandemirbas.nobetcieczane.domain.model.Base
import com.batuhandemirbas.nobetcieczane.domain.model.City
import com.batuhandemirbas.nobetcieczane.domain.model.County
import com.batuhandemirbas.nobetcieczane.domain.model.Pharmacy
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PharmacyService {

    @GET("/apiv2/pharmacy/distance?")
    fun getNearestPharmacy(@Query("apikey") apikey: String, @Query("latitude") latitude : Double, @Query("longitude",) longitude : Double): Call<Base<List<Pharmacy>>>

    @GET("/apiv2/pharmacyLink?")
    fun getPharmacy(@Query("apikey") apikey: String, @Query("city") city : String, @Query("county") county : String): Call<Base<List<Pharmacy>>>

    @GET("/apiv2/pharmacy/city?")
    fun getCities(@Query("apikey") apikey: String): Call<Base<Array<City>>>

    @GET("/apiv2/pharmacy/city/?")
    fun getCounty(@Query("apikey") apikey: String, @Query("city") city: String): Call<Base<Array<County>>>

}