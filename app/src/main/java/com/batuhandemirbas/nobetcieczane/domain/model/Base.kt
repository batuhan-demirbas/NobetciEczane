package com.batuhandemirbas.nobetcieczane.domain.model

data class Base<T>(
    val status: String?,
    val message: String?,
    val rowCount: Int?,
    val data: T?,
)