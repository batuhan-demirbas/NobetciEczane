package com.batuhandemirbas.nobetcieczane.domain.model

import com.google.gson.annotations.SerializedName

data class County(
    @SerializedName("ilceAd")
    val name: String? = null,

    @SerializedName("ilceSlug")
    val slug: String? = null
)
