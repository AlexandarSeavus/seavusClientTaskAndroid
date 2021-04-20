package com.seavus.arabamisat.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.seavus.arabamisat.model.Car
import com.seavus.arabamisat.repository.FirebaseRepository
import com.seavus.arabamisat.repository.LocalDBRepository

class CarsViewModel(application: Application) : AndroidViewModel(application) {
    private var firbaseRepository: FirebaseRepository
    private var localDbRepository: LocalDBRepository

    init {
        firbaseRepository = FirebaseRepository()
        localDbRepository = LocalDBRepository(application)
    }
    // FIREBASE ==================================================================================

    fun uploadToFirebase(uri: Uri, context: Context) {
        firbaseRepository.uploadToFirebase(uri, context)
    }

    fun getCars() {
        firbaseRepository.getCars()
    }

    // LOCAL DB ==================================================================================

    fun addCarToLocalDB(car: Car) {
        localDbRepository.addCar(car)
    }

    fun addCarListToLocalDB(carList: List<Car>) {
        localDbRepository.addAllCar(carList)
    }
    fun deleteCarListToLocalDB() {
        localDbRepository.deleteAllCar()
    }

    fun getCarFromLocalDB() {
        localDbRepository.getAllCars()
    }


    fun getLocalDBCarsResponseMutableLiveData(): LiveData<List<Car>> {
        return localDbRepository.getLocalDBCarsResponseMutableLiveData()
    }

    fun getInsertCartLocalDBResponseMutableLiveData(): LiveData<Boolean> {
        return localDbRepository.getInsertCartLocalDBResponseMutableLiveData()
    }


    fun getUploadResponseMutableLiveData(): LiveData<Uri> {
        return firbaseRepository.getUploadResponseMutableLiveData()
    }

    fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Car>> {
        return firbaseRepository.getCarsResponseMutableLiveData()
    }

    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return firbaseRepository.getOnProgressChangedLiveData()
    }

    fun clearObservers() {
        localDbRepository.clearDisposable()
    }
}