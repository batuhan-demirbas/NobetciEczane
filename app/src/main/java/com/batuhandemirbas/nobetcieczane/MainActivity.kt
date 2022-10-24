package com.batuhandemirbas.nobetcieczane

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.ace1ofspades.fragmentnavigation.BaseFragmentActivityBinding
import com.batuhandemirbas.MainViewHolder
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FilterDialogFragment : DialogFragment() {

    private val pharmacyApiKey = BuildConfig.PHARMACY_APIKEY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.dialog_filter, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DialogFragment onViewCreated")

        val cityMenu = view.findViewById<MaterialAutoCompleteTextView>(R.id.sss)
        val cities = mutableListOf<String>()
        Constants.city.list?.forEach { cities.add(it.name!!) }

        val adapter = ArrayAdapter(requireContext(), R.layout.item_cities, cities)

        cityMenu.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Snackbar.make(view, "${cities[p2]}", Snackbar.LENGTH_SHORT).show()


            }

        }

        (cityMenu as? AutoCompleteTextView)?.setAdapter(adapter)


        mainActivity = context as? MainActivity
        view.findViewById<Button>(R.id.buttonApply).setOnClickListener {
            Constants.user.city = view.findViewById<EditText>(R.id.textFieldCity).text.toString()
            Constants.user.county = view.findViewById<EditText>(R.id.textFieldCounty).text.toString()
            Snackbar.make(view, "asdasd", Snackbar.LENGTH_SHORT).show()
            getPharmacyData(Constants.user.city!!, Constants.user.county!!)
            parentFragmentManager.popBackStack()

        }
    }

    override fun onResume() {
        super.onResume()

        println("DialogFragment onResume")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("DialogFragment onDestroy")
    }

    var mainActivity: MainActivity? = null

    private fun getPharmacyData(city: String, county: String) {

        val call = RetrofitClient.retrofitInterface(context).getPharmacy(pharmacyApiKey,city, county)

        call.enqueue(object : Callback<Base<List<Pharmacy>>> {
            override fun onResponse(
                call: Call<Base<List<Pharmacy>>>,
                response: Response<Base<List<Pharmacy>>>
            ) {

                if (response.code() == 200) {
                    val pharmacyList = response.body()

                    pharmacyList?.data?.let { it ->

                        Constants.pharmacy.list = it
                        mainActivity?.configureFilter()
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

class MainActivity : BaseFragmentActivityBinding<ActivityMainBinding>() {

    val viewModel: MainViewHolder by viewModels()

    private val pharmacyApiKey = BuildConfig.PHARMACY_APIKEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        println("MAINACTIVITY onCreate")

        /*
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    // Update UI elements
                }
            }
        }
         */

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
        val fragmentManager = supportFragmentManager
        val newFragment = FilterDialogFragment()

        val isLargeLayout = resources.getBoolean(R.bool.large_layout)

        if (isLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog")
        } else {
            // The device is smaller, so show the fragment fullscreen
            val transaction = fragmentManager.beginTransaction()
            // For a little polish, specify a transition animation
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
        }

        // TODO: filtre uygulandıktan sonra veri çekilecek

    }

    private fun getPharmacyData(latitude: Double, longitude: Double) {

        val call = RetrofitClient.retrofitInterface(this).getNearestPharmacy(pharmacyApiKey,latitude, longitude)

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

        val call = RetrofitClient.retrofitInterface(this).getCities(pharmacyApiKey)

        call.enqueue(object : Callback<Base<Array<City>>> {
            override fun onResponse(
                call: Call<Base<Array<City>>>,
                response: Response<Base<Array<City>>>
            ) {

                if (response.code() == 200) {
                    val pharmacyList = response.body()

                    pharmacyList?.data?.let {

                        Constants.city.list = it
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