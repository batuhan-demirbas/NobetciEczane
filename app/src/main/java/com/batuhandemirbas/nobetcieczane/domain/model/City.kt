package com.batuhandemirbas.nobetcieczane.domain.model

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("SehirAd")
    val name: String? = null,

    @SerializedName("SehirSlug")
    val slug: String? = null
)


