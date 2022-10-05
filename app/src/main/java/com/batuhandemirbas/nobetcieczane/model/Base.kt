package com.batuhandemirbas.nobetcieczane.model

data class Base<T>(
    val status: String?,
    val message: String?,
    val rowCount: Int?,
    val data: T?,
) {

}