package com.batuhandemirbas.nobetcieczane

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.room.Room
import com.batuhandemirbas.nobetcieczane.data.local.AppDatabase
import com.batuhandemirbas.nobetcieczane.data.local.CityEntity
import com.batuhandemirbas.nobetcieczane.databinding.ActivityMainBinding
import com.batuhandemirbas.nobetcieczane.domain.model.Base
import com.batuhandemirbas.nobetcieczane.domain.model.City
import com.batuhandemirbas.nobetcieczane.domain.model.Pharmacy
import com.batuhandemirbas.nobetcieczane.data.remote.RetrofitClient
import com.batuhandemirbas.nobetcieczane.util.Constants
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pharmacyApiKey = BuildConfig.PHARMACY_APIKEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        println("MAINACTIVITY onCreate")

        getCityData()

        locationPermissionRequest()

        LocationUpdates().getUserLocation(this, binding.bottomNavigation) {

            if (it != null) {
                Constants.user.Lat = it.latitude
                Constants.user.Lon = it.longitude

                configure()
            }
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {

            when (resultCode) {
                0 -> {
                    showDialogFilter()

                }

                -1 -> {
                    getUserLocation(this, completion = {
                        Constants.user.Lat = it.latitude
                        Constants.user.Lon = it.longitude
                        configure()
                        getPharmacyData(it.latitude, it.longitude)
                    })


                }

            }
        }

    }

    fun configureFilter() {
        println("ConfigureFilter Start")
        Constants.user.city ?: return
        Constants.pharmacy.list ?: return


        binding.progressBar.visibility = View.GONE

        /*
        navView = binding.bottomNavigation
        tab1.fragment.startDestination = R.id.mapFragment
        tab1.fragment.graphId = R.navigation.nav_graph
        tab2.fragment.startDestination = R.id.nearestFragment
        tab2.fragment.graphId = R.navigation.nav_graph
        tab3.fragment.startDestination = R.id.calenderFragment
        tab3.fragment.graphId = R.navigation.nav_graph
        tab4.fragment.startDestination = R.id.bookmarkFragment
        tab4.fragment.graphId = R.navigation.nav_graph
        containerId = R.id.fragmentContainerView

         */
    }

    override fun onResume() {
        super.onResume()

        println("MAINACTIVITY onResume")

    }

    override fun onStop() {
        super.onStop()

        println("MAINACTIVITY onStop")
    }

    private fun locationPermissionRequest() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.

                    isGPSEnable(this)


                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.

                    isGPSEnable(this)


                }
                else -> {
                    // No location access granted.

                    showDialogFilter()
                }
            }
        }

        // ...

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    }

    private fun isGPSEnable(activity: Activity) {

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = locationRequest.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = builder.let {
            client.checkLocationSettings(
                it.build()
            )
        }

        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here
            // ...

            getUserLocation(this, completion = {
                getPharmacyData(it.latitude, it.longitude)
            })


        }

        task.addOnFailureListener { exception ->
            print("cancel gps")

            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(activity, 1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun showDialogFilter() {
       findNavController(R.id.fragmentContainerView).navigate(R.id.action_splashFragment_to_filterFragment)

    }

    private fun getPharmacyData(latitude: Double, longitude: Double) {

        val call = RetrofitClient.retrofitInterface()
            .getNearestPharmacy(pharmacyApiKey, latitude, longitude)

        call.enqueue(object : Callback<Base<List<Pharmacy>>> {
            override fun onResponse(
                call: Call<Base<List<Pharmacy>>>,
                response: Response<Base<List<Pharmacy>>>
            ) {

                if (response.code() == 200) {
                    val pharmacyList = response.body()

                    pharmacyList?.data?.let {

                        Constants.pharmacy.list = it

                        configure()
                    }
                }
            }

            override fun onFailure(call: Call<Base<List<Pharmacy>>>, t: Throwable) {
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

    private fun getCityData() {

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "city"
        ).allowMainThreadQueries().build()

        val cityDao = db.cityDao()



            val call = RetrofitClient.retrofitInterface().getCities(pharmacyApiKey)

            call.enqueue(object : Callback<Base<Array<City>>> {
                override fun onResponse(
                    call: Call<Base<Array<City>>>,
                    response: Response<Base<Array<City>>>
                ) {

                    if (response.code() == 200) {
                        val pharmacyList = response.body()

                        pharmacyList?.data?.let {

                            Constants.city.list = it

                            if (cityDao.getDataCount() == 0) {

                            for (i in it) {
                                cityDao.insert(CityEntity(0, i.name, i.slug))
                            }

                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Base<Array<City>>>, t: Throwable) {
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

    private fun getUserLocation(activity: Activity, completion: (location: Location) -> Unit) {

        val fusedLocationClient =
            activity.let { LocationServices.getFusedLocationProviderClient(it) }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }


        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location ->
                completion(location)

            }
    }

    private fun configure() {

        Constants.user.Lat ?: return
        Constants.user.Lon ?: return
        Constants.pharmacy.list ?: return

        binding.progressBar.visibility = View.GONE
        /*
        navView = binding.bottomNavigation
        tab1.fragment.startDestination = R.id.mapFragment
        tab1.fragment.graphId = R.navigation.nav_graph
        tab2.fragment.startDestination = R.id.nearestFragment
        tab2.fragment.graphId = R.navigation.nav_graph
        tab3.fragment.startDestination = R.id.calenderFragment
        tab3.fragment.graphId = R.navigation.nav_graph
        tab4.fragment.startDestination = R.id.bookmarkFragment
        tab4.fragment.graphId = R.navigation.nav_graph
        containerId = R.id.fragmentContainerView

         */
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