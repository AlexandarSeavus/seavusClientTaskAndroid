package com.seavus.arabamisat.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.seavus.arabamisat.model.Vehicle
import com.seavus.arabamisat.repository.FirebaseRepoImpl
import com.seavus.arabamisat.repository.IDBRepo
import com.seavus.arabamisat.repository.IFirebaseRepo
import com.seavus.arabamisat.repository.LocalDBRepoImpl
import kotlinx.coroutines.launch

class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    private var firebaseRepo: IFirebaseRepo = FirebaseRepoImpl(application)

    private var localDBRepository: IDBRepo = LocalDBRepoImpl(application)


    val uploadResponse: LiveData<Uri>
        get() = _uploadResponse

    private val _uploadResponse by lazy {
        MutableLiveData<Uri>()
    }

    // FIREBASE ==================================================================================

    fun uploadToFirebase(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uploadResponse.value = firebaseRepo.uploadToFirebase(uri, context)
        }
    }

    fun getCars() {
        firebaseRepo.getCars()
    }

    // LOCAL DB ==================================================================================

    fun addCarToLocalDB(vehicle: Vehicle) {
        localDBRepository.addCar(vehicle)
    }

    fun addCarListToLocalDB(vehicleList: List<Vehicle>) {
        localDBRepository.addAllCars(vehicleList)
    }

    fun deleteCarListToLocalDB() {
        localDBRepository.deleteAllCars()
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

    fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Vehicle>> {
        return firebaseRepo.getCarsResponseMutableLiveData()
    }

    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return firebaseRepo.getOnProgressChangedLiveData()
    }

    fun clearObservers() {
        localDBRepository.clearDisposable()
    }
}