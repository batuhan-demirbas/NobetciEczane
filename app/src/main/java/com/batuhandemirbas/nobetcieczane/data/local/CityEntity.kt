package com.batuhandemirbas.nobetcieczane.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "city_name") val cityName: String?,
    @ColumnInfo(name = "city_slug") val citySlug: String?
)