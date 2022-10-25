package com.batuhandemirbas.nobetcieczane.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAll(): List<CityEntity>

    @Query("SELECT * FROM city WHERE uid IN (:cityIds)")
    fun loadAllByIds(cityIds: IntArray): List<CityEntity>

    @Query("SELECT * FROM city WHERE city LIKE :city AND " +
            "city_slug LIKE :slug LIMIT 1")
    fun findByName(city: String, slug: String): CityEntity

    @Query("SELECT COUNT(uid) FROM city")
    fun getDataCount(): Int

    @Insert
    fun insert( cities: CityEntity)

    @Insert
    fun insertAll(vararg cities: CityEntity)

    @Delete
    fun delete(city: CityEntity)

}
