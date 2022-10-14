package com.batuhandemirbas.nobetcieczane.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.batuhandemirbas.nobetcieczane.LocationUpdates
import com.batuhandemirbas.nobetcieczane.R
import com.batuhandemirbas.nobetcieczane.databinding.FragmentMapBinding
import com.batuhandemirbas.nobetcieczane.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val mapkitApiKey = "942efc88-0b2b-40d9-b5cd-edb08f1eabe5"
    private var mapView: MapView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(mapkitApiKey)
        MapKitFactory.initialize(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("MAP OnViewCreated")

        val userLocation = Constants.user

        mapView = view.findViewById(R.id.mapview) as MapView


        if (userLocation.Lat == null) {

            val randomPharmacy = Constants.pharmacy.list?.get(0)

            val countyLocation = Point(randomPharmacy?.latitude!!, randomPharmacy.longitude!!)

            mapView!!.map.move(
                CameraPosition(countyLocation, 12.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2F),
                null
            )



        } else {

            val lat = userLocation.Lat
            val lon = userLocation.Lon

            val currentLocation = Point(lat?:0.0, lon?:0.0)

            val mapObjects = mapView!!.map.mapObjects.addCollection()
            mapObjects.addPlacemark(Point(lat?:0.0, lon?:0.0))

            mapView!!.map.move(
                CameraPosition(currentLocation, 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2F),
                null
            )

            binding.fab.setOnClickListener {

                mapView!!.map.move(
                    CameraPosition(currentLocation, 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 2F),
                    null
                )

            }

            Snackbar.make(view, "Succes get location.", Snackbar.LENGTH_SHORT)
                .show()

        }


    }

    override fun onResume() {
        super.onResume()

        println("MAP onResume")

        mapView = binding.mapview
        val pharmacyList = Constants.pharmacy.list

        if (pharmacyList != null) {
            for (pharmacy in pharmacyList) {
                val mapObjects = mapView!!.map.mapObjects.addCollection()
                val mark: PlacemarkMapObject = mapObjects.addPlacemark(
                    Point(
                        pharmacy.latitude!!,
                        pharmacy.longitude!!
                    )
                )

                val icon = ImageProvider.fromResource(context, R.drawable.pin_filled)
                with(mark) {

                    setIcon(icon)
                }

                mapView!!.map.mapObjects.addTapListener { mapObject, point ->
                    true
                }

            }
        }

    }

    override fun onStop() {
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()

        println("MAP onStop")
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView?.onStart()

        println("MAP onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}