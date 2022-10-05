package com.batuhandemirbas.nobetcieczane.model

import com.google.gson.annotations.SerializedName

class Pharmacy:java.io.Serializable {
    @SerializedName("EczaneAdi")
    val pharmacyName: String? = null

    @SerializedName("Adresi")
    val pharmacyAddress: String? = null

    @SerializedName("Semt")
    val pharmacyDistrict: String? = null

    @SerializedName("YolTarifi")
    val pharmacyDirection: String? = null

    @SerializedName("Telefon")
    val pharmacyPhone: String? = null

    @SerializedName("Telefon2")
    val pharmacyPhone2: String? = null

    @SerializedName("Sehir")
    val pharmacyCity: String? = null

    @SerializedName("ilce")
    val pharmacyCounty: String? = null

    @SerializedName("latitude")
    val pharmacyLatitude: Double? = null

    @SerializedName("longitude")
    val pharmacyLongitude: Double? = null

}