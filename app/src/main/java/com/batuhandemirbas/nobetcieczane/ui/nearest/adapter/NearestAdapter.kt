package com.batuhandemirbas.nobetcieczane.ui.nearest.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.batuhandemirbas.nobetcieczane.LocationUpdates
import com.batuhandemirbas.nobetcieczane.R
import com.batuhandemirbas.nobetcieczane.domain.model.City
import com.batuhandemirbas.nobetcieczane.domain.model.Pharmacy
import com.batuhandemirbas.nobetcieczane.util.Constants

class NearestAdapter(private val dataSet: List<Pharmacy>) :
    RecyclerView.Adapter<NearestAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val address: TextView
        val phone: TextView
        val distance: TextView
        val distances: Button
        val call: Button

        init {
            // Define click listener for the ViewHolder's View.
            name = view.findViewById(R.id.name)
            address = view.findViewById(R.id.address)
            phone = view.findViewById(R.id.phone)
            distance = view.findViewById(R.id.distance)
            distances = view.findViewById(R.id.directionsButton)
            call = view.findViewById(R.id.callButton)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_pharmacy, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        val pharmacy = dataSet[position]

        with(viewHolder) {
            name.text = pharmacy.name
            address.text = pharmacy.address

            if (Constants.user.Lat == null) {
                distance.visibility = View.GONE
            } else {
                distance.text = String.format("%.2f km uzaklıkta", pharmacy.distance)
                // .toDouble()"${}m uzaklıkta"
            }

            phone.text = pharmacy.phone?.replace("-", " ")?.replace("(", " (")?.replace(")", ") ")

            distances.setOnClickListener {
                LocationUpdates().goMaps(
                    it.context,
                    pharmacy.latitude!!,
                    pharmacy.longitude!!,
                    pharmacy.name!!
                )
            }

            call.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + "${pharmacy.phone}")
                startActivity(it.context, dialIntent, Bundle())
            }
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}

class CityAdapter(val context: Context, val cities: Array<City>) : BaseAdapter(),
    ThemedSpinnerAdapter, Filterable {
    override fun getCount(): Int {
        return cities.size
    }

    override fun getItem(p0: Int): Any {
        return cities[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cities, p2)
        return view
    }

    override fun setDropDownViewTheme(p0: Resources.Theme?) {

    }

    override fun getDropDownViewTheme(): Resources.Theme? {
        return null
    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                TODO("Not yet implemented")
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                TODO("Not yet implemented")
            }

        }
    }

}



