package com.batuhandemirbas.nobetcieczane.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitInterface {

    @GET("/apiv2/pharmacyLink?city=bursa&county=nilufer&apikey=I5mgwVxH9RN3aRcYmvDjNwBdiO53z4XRtzGKA5QQfg1wctSAStfpYDjAu50e")
    fun getPharmacyData(): Call<JsonObject>
}