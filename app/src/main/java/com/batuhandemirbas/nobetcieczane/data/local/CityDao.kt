package com.batuhandemirbas.nobetcieczane.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.batuhandemirbas.nobetcieczane.domain.model.City
import java.util.ListResourceBundle

@Dao
interface CityDao {
    @Query("SELECT * FROM city_table")
    fun getAll(): List<CityEntity>

    @Query("SELECT * FROM city_table WHERE uid IN (:cityIds)")
    fun loadAllByIds(cityIds: IntArray): List<CityEntity>

    @Query("SELECT city_name FROM city_table")
    fun loadAllCityName(): Array<String>

    @Query("SELECT * FROM city_table WHERE city_slug")
    fun loadAllCitySlug(): Array<CityEntity>

    @Query("SELECT * FROM city_table WHERE city_name LIKE :city AND " +
            "city_slug LIKE :slug LIMIT 1")
    fun findByName(city: String, slug: String): CityEntity

    @Query("SELECT COUNT(uid) FROM city_table")
    fun getDataCount(): Int

    @Insert
    fun insert( cities: CityEntity)

    @Insert
    fun insertAll(vararg cities: CityEntity)

    @Delete
    fun delete(city: CityEntity)

}
