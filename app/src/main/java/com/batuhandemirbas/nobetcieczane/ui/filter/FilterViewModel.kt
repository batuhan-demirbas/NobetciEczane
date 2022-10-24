package com.batuhandemirbas.nobetcieczane.ui.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.batuhandemirbas.nobetcieczane.BuildConfig
import com.batuhandemirbas.nobetcieczane.data.remote.RetrofitClient
import com.batuhandemirbas.nobetcieczane.domain.model.Base
import com.batuhandemirbas.nobetcieczane.domain.model.City
import com.batuhandemirbas.nobetcieczane.domain.model.County
import com.batuhandemirbas.nobetcieczane.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

data class FilterUiState(
    val lat: Double? = null,
    val lon: Double? = null,
)

class FilterViewModel : ViewModel() {

    private val pharmacyApiKey = BuildConfig.PHARMACY_APIKEY

    var countyArray: Array<County>? = null

    val cityList: MutableList<String> = mutableListOf()
    val countyList: MutableList<String> = mutableListOf()
    var list = MutableLiveData<List<String>>()

    // Expose screen UI state
    //private val _uiState = MutableStateFlow(FilterUiState())
    //val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    // Handle business logic

    fun getCity(): Array<String> {
        Constants.city.list?.forEach { cityList.add(it.name!!) }
        return cityList.toTypedArray()
    }

    fun getCounty(city: String) {

        val call = RetrofitClient.retrofitInterface().getCounty(pharmacyApiKey, city)

        call.enqueue(object : Callback<Base<Array<County>>> {
            override fun onResponse(
                call: Call<Base<Array<County>>>,
                response: Response<Base<Array<County>>>
            ) {

                if (response.code() == 200) {
                    val pharmacyList = response.body()


                    pharmacyList?.data?.let {
                        countyArray = it
                    }
                    countyList.clear()
                    countyArray?.forEach { countyList.add(it.name!!) }
                    list.postValue(countyList)
                }
            }

            override fun onFailure(call: Call<Base<Array<County>>>, t: Throwable) {
                println("-----------BEGIN---------")
                println(" ")
                println("URL      ->" + call.request().url())
                println("METHOD   ->" + call.request().method())
                println("HEADER   ->" + call.request().headers())
                if (call.request().body() != null) {
                    println("REQUEST  ->" + bodyToString(call.request().body()!!))
                } else {
                    println("REQUEST  -> null")
                }
                println(" ")
                println("------------END----------")
                println("error")
                println(t.message)
            }

        })

    }

    private fun bodyToString(request: RequestBody): String {
        return try {
            val buffer = Buffer()
            request.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }
}