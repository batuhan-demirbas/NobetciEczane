package com.batuhandemirbas.nobetcieczane.ui.filter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.batuhandemirbas.nobetcieczane.BuildConfig
import com.batuhandemirbas.nobetcieczane.MainActivity
import com.batuhandemirbas.nobetcieczane.R
import com.batuhandemirbas.nobetcieczane.data.remote.RetrofitClient
import com.batuhandemirbas.nobetcieczane.domain.model.Base
import com.batuhandemirbas.nobetcieczane.domain.model.Pharmacy
import com.batuhandemirbas.nobetcieczane.util.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FilterFragment : DialogFragment() {

    private val pharmacyApiKey = BuildConfig.PHARMACY_APIKEY
    val viewModel: FilterViewModel by viewModels()


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

        val cityMenu = view.findViewById<MaterialAutoCompleteTextView>(R.id.city)
        val countyMenu = view.findViewById<MaterialAutoCompleteTextView>(R.id.county)

        val cityItems = viewModel.getCity()
        cityMenu?.setSimpleItems(cityItems)


        cityMenu.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Snackbar.make(view, "${cityItems[position]}", Snackbar.LENGTH_SHORT).show()
                val selectedCity= Constants.city.list?.get(position)?.slug
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
        view.findViewById<Button>(R.id.buttonApply).setOnClickListener {
            Snackbar.make(view, "mission complate", Snackbar.LENGTH_SHORT).show()
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

    fun configure() {
        if(viewModel.cityList.isEmpty()) return configure()
    }

}