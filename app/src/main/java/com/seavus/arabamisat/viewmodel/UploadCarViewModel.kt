package com.seavus.arabamisat.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.seavus.arabamisat.model.Car
import com.seavus.arabamisat.repository.FirebaseRepository

class UploadCarViewModel : ViewModel() {
    private var firbaseRepository: FirebaseRepository

    init {
        firbaseRepository = FirebaseRepository()
    }

    fun uploadToFirebase(uri: Uri, context: Context) {
        firbaseRepository.uploadToFirebase(uri, context)
    }

    fun getCars() {
        firbaseRepository.getCars()
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
}