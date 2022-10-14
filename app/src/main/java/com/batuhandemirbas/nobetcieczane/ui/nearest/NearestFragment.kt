package com.batuhandemirbas.nobetcieczane.ui.nearest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.batuhandemirbas.nobetcieczane.LocationUpdates
import com.batuhandemirbas.nobetcieczane.databinding.FragmentNearestBinding
import com.batuhandemirbas.nobetcieczane.model.Base
import com.batuhandemirbas.nobetcieczane.model.Pharmacy
import com.batuhandemirbas.nobetcieczane.network.RetrofitClient
import com.batuhandemirbas.nobetcieczane.ui.nearest.adapter.NearestAdapter
import com.batuhandemirbas.nobetcieczane.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NearestFragment : Fragment() {

    private var _binding: FragmentNearestBinding? = null
    private val binding get() = _binding!!

    val args: NearestFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNearestBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("NEAREST OnViewCreated")


        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager


        binding.fabNearest.setOnClickListener {

        }



    }

    /*
    private fun checkUserLocationStatus() {

        if (Constants.user.Lat == null) {
            checkUserLocationStatus()
        }
    }

    */

    override fun onResume() {
        super.onResume()

        println("NEAREST onResume")

        val user = Constants.user
        val pharmacyList = Constants.pharmacy.list

        if (pharmacyList != null) {
            for(i in pharmacyList) {

                val distance = LocationUpdates().distanceInKm(i.latitude ?: 0.0, i.longitude ?: 0.0, user.Lat ?: 0.0, user.Lon ?: 0.0)
                i.distance =  distance
            }
        }

        val sortedList = pharmacyList?.sortedBy { Pharmacy ->
            Pharmacy.distance
        }

        binding.recyclerView.adapter = sortedList?.let { NearestAdapter(it) }


    }

    override fun onPause() {
        super.onPause()
        println("NEAREST onPause")

    }

    override fun onStop() {
        super.onStop()
        println("NEAREST onStop")
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}