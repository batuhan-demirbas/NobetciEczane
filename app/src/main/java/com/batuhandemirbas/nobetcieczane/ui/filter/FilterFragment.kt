package com.batuhandemirbas.nobetcieczane.ui.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.batuhandemirbas.nobetcieczane.BuildConfig
import com.batuhandemirbas.nobetcieczane.MainActivity
import com.batuhandemirbas.nobetcieczane.R
import com.batuhandemirbas.nobetcieczane.data.local.AppDatabase
import com.batuhandemirbas.nobetcieczane.data.remote.RetrofitClient
import com.batuhandemirbas.nobetcieczane.domain.model.Base
import com.batuhandemirbas.nobetcieczane.domain.model.Pharmacy
import com.batuhandemirbas.nobetcieczane.util.Constants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FilterFragment : Fragment() {

    private val pharmacyApiKey = BuildConfig.PHARMACY_APIKEY

    var mainActivity: MainActivity? = null
    val viewModel: FilterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        val cityDao = db.cityDao()

        val cityMenu = view.findViewById<MaterialAutoCompleteTextView>(R.id.city)
        val countyMenu = view.findViewById<MaterialAutoCompleteTextView>(R.id.county)

        val cityItems = viewModel.getCity()
        cityMenu?.setSimpleItems(cityItems)

        cityMenu.onItemClickListener = object : AdapterView.OnItemClickListener {
            @SuppressLint("FragmentLiveDataObserve")
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Snackbar.make(view, "${position}", Snackbar.LENGTH_SHORT).show()
                val selectedCity = Constants.city.list?.get(position)?.slug
                if (selectedCity != null) {
                    viewModel.getCounty(selectedCity)
                }

                view.findViewById<LinearLayout>(R.id.menuCounty).visibility = View.VISIBLE

                Constants.user.city = Constants.city.list?.get(position)?.slug

                viewModel.list.observe(this@FilterFragment, Observer {
                    val countyItems = viewModel.countyList.toTypedArray()
                    countyMenu?.setSimpleItems(countyItems)
                })

            }

        }

        countyMenu.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Constants.user.county = viewModel.countyArray?.get(position)?.slug
            }

        }

        mainActivity = context as? MainActivity

        val currentList: MutableLiveData<List<Pharmacy>> by lazy {
            MutableLiveData<List<Pharmacy>>()
        }

        view.findViewById<Button>(R.id.buttonApply).setOnClickListener {
            Snackbar.make(view, "mission complate", Snackbar.LENGTH_SHORT).show()
            getPharmacyData(Constants.user.city!!, Constants.user.county!!, currentList)



            val nameObserver = Observer<List<Pharmacy>> { newName ->
                // Update the UI, in this case, a TextView.
                mainActivity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.VISIBLE
                mainActivity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
                    View.VISIBLE
                findNavController().navigate(R.id.action_filterFragment_to_mapFragment)
            }

            // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
            currentList.observe(viewLifecycleOwner, nameObserver)





        }

    }

    private fun getPharmacyData(city: String, county: String, currentList: MutableLiveData<List<Pharmacy>>) {

        val call = RetrofitClient.retrofitInterface().getPharmacy(pharmacyApiKey, city, county)

        call.enqueue(object : Callback<Base<List<Pharmacy>>> {
            override fun onResponse(
                call: Call<Base<List<Pharmacy>>>,
                response: Response<Base<List<Pharmacy>>>
            ) {

                if (response.code() == 200) {
                    val pharmacyList = response.body()

                    pharmacyList?.data?.let { it ->

                        Constants.pharmacy.list = it
                        currentList.value = it
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