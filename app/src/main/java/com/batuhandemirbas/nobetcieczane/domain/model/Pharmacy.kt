package com.batuhandemirbas.nobetcieczane.domain.model

import com.google.gson.annotations.SerializedName

data class Pharmacy(
    @SerializedName("EczaneAdi")
    val name: String? = null,

    @SerializedName("Adresi")
    val address: String? = null,

    @SerializedName("Semt")
    val district: String? = null,

    @SerializedName("YolTarifi")
    val direction: String? = null,

    @SerializedName("Telefon")
    val phone: String? = null,

    @SerializedName("Telefon2")
    val phone2: String? = null,

    @SerializedName("Sehir")
    val city: String? = null,

    @SerializedName("ilce")
    val county: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    var distance: Double? = null
)