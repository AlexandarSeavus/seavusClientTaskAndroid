package com.seavus.arabamisat.db

import androidx.room.*
import com.seavus.arabamisat.model.Car
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single


@Dao
interface CarDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(car: Car?): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCars(carList: List<Car>): Completable

    @Update
    fun update(car: Car?): Single<Int>

    @Query("DELETE FROM cars_table")
    fun delete(): Completable

    @Query("SELECT * FROM cars_table")
    fun getAllCars(): Maybe<List<Car>>

    @Query("SELECT COUNT() FROM cars_table  WHERE car_id = :carId")
    fun count(carId: Int): Maybe<Int>

    @Query("SELECT * FROM cars_table WHERE synced = :synced")
    fun getUnsyncedCars(synced: Boolean = true): Maybe<List<Car>>
}