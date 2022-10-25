package com.batuhandemirbas.nobetcieczane.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "city_slug") val citySlug: String?
)