package com.batuhandemirbas.nobetcieczane.util

import com.batuhandemirbas.nobetcieczane.domain.model.City
import com.batuhandemirbas.nobetcieczane.domain.model.Pharmacy

object Constants {
    object user {
        var Lat: Double? = null
        var Lon: Double? = null
        var city: String? = null
        var county: String? = null
    }

    object pharmacy {
        var list: List<Pharmacy>? = null
    }

    object city {
        var list: Array<City>? = null
    }

}