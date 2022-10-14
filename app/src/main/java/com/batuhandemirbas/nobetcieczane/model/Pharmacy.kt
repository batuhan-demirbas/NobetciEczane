package com.batuhandemirbas.nobetcieczane.model

import com.google.gson.annotations.SerializedName

class Pharmacy:java.io.Serializable {
    @SerializedName("EczaneAdi")
    val name: String? = null

    @SerializedName("Adresi")
    val address: String? = null

    @SerializedName("Semt")
    val district: String? = null

    @SerializedName("YolTarifi")
    val direction: String? = null

    @SerializedName("Telefon")
    val phone: String? = null

    @SerializedName("Telefon2")
    val phone2: String? = null

    @SerializedName("Sehir")
    val city: String? = null

    @SerializedName("ilce")
    val county: String? = null

    @SerializedName("latitude")
    val latitude: Double? = null

    @SerializedName("longitude")
    val longitude: Double? = null

    var distance: Double? = null

}