package com.batuhandemirbas.nobetcieczane.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.batuhandemirbas.nobetcieczane.BuildConfig
import com.batuhandemirbas.nobetcieczane.databinding.FragmentMapBinding
import com.batuhandemirbas.nobetcieczane.util.Constants
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    val viewModel: MapViewModel by viewModels()

    private val mapApiKey = BuildConfig.MAP_APIKEY
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    // Update UI elements
                }
            }
        }

        MapKitFactory.setApiKey(mapApiKey)
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

        val user = viewModel.user

        mapView = binding.mapview

        if (user.Lat == null) {

            val randomPharmacy = Constants.pharmacy.list?.get(0)

            val countyLocation = Point(randomPharmacy?.latitude!!, randomPharmacy.longitude!!)

            mapView!!.map.move(
                CameraPosition(countyLocation, 12.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2F),
                null
            )

        } else {

            val lat = user.Lat
            val lon = user.Lon


            val mapObjects = mapView!!.map.mapObjects.addCollection()
            mapObjects.addPlacemark(Point(lat ?: 0.0, lon ?: 0.0))

            mapView!!.map.move(
                CameraPosition(viewModel.currentLocation, 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2F),
                null
            )

            binding.fab.setOnClickListener {

                mapView!!.map.move(
                    CameraPosition(viewModel.currentLocation, 14.0f, 0.0f, 0.0f),
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

                val icon = ImageProvider.fromResource(
                    context,
                    com.yandex.maps.mobile.R.drawable.yandex_logo_en
                )
                mark.setIcon(icon)

                mark.addTapListener { mapObject, point ->
                    Snackbar.make(requireView(), "${point.latitude}", Snackbar.LENGTH_SHORT).show()

                    false
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