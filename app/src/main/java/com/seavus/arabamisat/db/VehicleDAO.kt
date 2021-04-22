package com.seavus.arabamisat.db

import androidx.room.*
import com.seavus.arabamisat.model.Vehicle
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
interface VehicleDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: Vehicle?): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCars(vehicleList: List<Vehicle>): Completable

    @Update
    fun update(vehicle: Vehicle?): Single<Int>

    @Query("DELETE FROM cars_table")
    fun delete(): Completable

    @Query("SELECT * FROM cars_table")
    fun getAllCars(): Maybe<List<Vehicle>>

    @Query("SELECT COUNT() FROM cars_table  WHERE car_id = :carId")
    fun userCount(carId: String): Maybe<Int>

    @Query("SELECT COUNT() FROM cars_table")
    fun userCount(): Maybe<Int>

    @Query("SELECT * FROM cars_table WHERE synced = :synced")
    fun getUnsyncedCars(synced: Boolean = true): Maybe<List<Vehicle>>
}