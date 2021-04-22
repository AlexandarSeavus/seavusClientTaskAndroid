package com.seavus.arabamisat.repository

import androidx.lifecycle.LiveData
import com.seavus.arabamisat.model.Vehicle

interface IDBRepo {
    fun addCar(vehicle: Vehicle)

    fun addAllCars(vehicleList: List<Vehicle>)

    fun deleteAllCars()

    fun getAllCars()

    fun getUnsyncedCars()

    fun getLocalDBCarsResponseMutableLiveData(): LiveData<List<Vehicle>>

    fun getUnsyncedCarsResponseMutableLiveData(): LiveData<List<Vehicle>>

    fun getOnProgressChangedLiveData(): LiveData<Boolean>

    fun getInsertCartLocalDBResponseMutableLiveData(): LiveData<Boolean>

    fun clearDisposable()
}