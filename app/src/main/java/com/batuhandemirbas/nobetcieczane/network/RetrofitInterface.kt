package com.batuhandemirbas.nobetcieczane.network

import com.batuhandemirbas.nobetcieczane.model.Base
import com.batuhandemirbas.nobetcieczane.model.Pharmacy
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("/apiv2/pharmacy/distance?apikey=I5mgwVxH9RN3aRcYmvDjNwBdiO53z4XRtzGKA5QQfg1wctSAStfpYDjAu50e")
    fun getPharmacyData(@Query("latitude") latitude : Double, @Query("longitude") longitude : Double): Call<Base<List<Pharmacy>>>

    @GET("/apiv2/pharmacyLink?apikey=I5mgwVxH9RN3aRcYmvDjNwBdiO53z4XRtzGKA5QQfg1wctSAStfpYDjAu50e")
    fun getPharmacyData(@Query("city") city : String, @Query("county") county : String): Call<Base<List<Pharmacy>>>

    // @Query("city") city : String, @Query("county") county : String
}