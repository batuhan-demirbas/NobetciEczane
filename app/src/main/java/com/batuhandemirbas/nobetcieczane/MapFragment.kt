package com.batuhandemirbas.nobetcieczane

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.batuhandemirbas.alertdialog.AlertDialogs
import com.batuhandemirbas.nobetcieczane.model.Base
import com.batuhandemirbas.nobetcieczane.model.Pharmacy
import com.batuhandemirbas.nobetcieczane.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject

import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MapFragment : Fragment() {

    private val MAPKIT_API_KEY = "942efc88-0b2b-40d9-b5cd-edb08f1eabe5"


    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val call = RetrofitClient.retrofitInterface(context).getPharmacyData()

        AlertDialogs().presentAlert(requireContext(), "Lütfen konum bilginizi giriniz", "Konum Bilgisi")
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val pharmacyList = Gson().fromJson<Base<List<Pharmacy>>>(
                        response.body().toString(),
                        object : TypeToken<Base<List<Pharmacy>>>() {}.type
                    )
                    pharmacyList.data?.let {
                        val targetLocation: Point =
                            Point(it.first().pharmacyLatitude!!, it.first().pharmacyLongitude!!)

                        mapView = view.findViewById(R.id.mapview) as MapView

                        mapView!!.map.move(
                            CameraPosition(targetLocation, 14.0f, 0.0f, 0.0f),
                            Animation(Animation.Type.SMOOTH, 2F),
                            null
                        )
                        // mapView!!.map.isNightModeEnabled = true

                        for (pharmacy in it) {
                            val mapObjects = mapView!!.map.mapObjects.addCollection()
                            val mark: PlacemarkMapObject = mapObjects.addPlacemark(
                                Point(
                                    pharmacy.pharmacyLatitude!!,
                                    pharmacy.pharmacyLongitude!!
                                )
                            )

                            with(mark) {
                                isDraggable = true
                                setIcon(ImageProvider.fromResource(context, R.drawable.pin_filled))
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
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

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()
    }

}