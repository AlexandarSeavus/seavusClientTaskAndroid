package com.seavus.arabamisat.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.seavus.arabamisat.model.Vehicle
import com.seavus.arabamisat.repository.FirebaseRepository
import com.seavus.arabamisat.repository.LocalDBRepository

class CarsViewModel(application: Application) : AndroidViewModel(application) {
    private var firbaseRepository: FirebaseRepository
    private var localDBRepository: LocalDBRepository

    init {
        firbaseRepository = FirebaseRepository(application)
        localDBRepository = LocalDBRepository(application)
    }
    // FIREBASE ==================================================================================

    fun uploadToFirebase(uri: Uri, context: Context) {
        firbaseRepository.uploadToFirebase(uri, context)
    }

    fun getCars() {
        firbaseRepository.getCars()
    }

    // LOCAL DB ==================================================================================

    fun addCarToLocalDB(vehicle: Vehicle) {
        localDBRepository.addCar(vehicle)
    }

    fun addCarListToLocalDB(vehicleList: List<Vehicle>) {
        localDBRepository.addAllCar(vehicleList)
    }

    fun deleteCarListToLocalDB() {
        localDBRepository.deleteAllCar()
    }

    fun getCarFromLocalDB() {
        localDBRepository.getAllCars()
    }

    fun getUnsyncedCars() {
        localDBRepository.getUnsyncedCars()
    }

    // OBSERVERS =================================================================================

    fun getUnsyncedCarsResponseMutableLiveData(): LiveData<List<Vehicle>> {
        return localDBRepository.getUnsyncedCarsResponseMutableLiveData()
    }


    fun getLocalDBCarsResponseMutableLiveData(): LiveData<List<Vehicle>> {
        return localDBRepository.getLocalDBCarsResponseMutableLiveData()
    }

    fun getInsertCartLocalDBResponseMutableLiveData(): LiveData<Boolean> {
        return localDBRepository.getInsertCartLocalDBResponseMutableLiveData()
    }


    fun getUploadResponseMutableLiveData(): LiveData<Uri> {
        return firbaseRepository.getUploadResponseMutableLiveData()
    }

    fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Vehicle>> {
        return firbaseRepository.getCarsResponseMutableLiveData()
    }

    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return firbaseRepository.getOnProgressChangedLiveData()
    }

    fun clearObservers() {
        localDBRepository.clearDisposable()
    }
}