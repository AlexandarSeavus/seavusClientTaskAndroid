package com.seavus.arabamisat.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.seavus.arabamisat.repository.FirebaseRepository

class UploadCarViewModel : ViewModel() {
    private var firbaseRepository: FirebaseRepository

    init {
        firbaseRepository = FirebaseRepository()
    }

    fun uploadToFirebase(uri: Uri, context: Context) {
        firbaseRepository.uploadToFirebase(uri, context)
    }


    fun getUploadResponseMutableLiveData(): LiveData<Uri> {
        return firbaseRepository.getUploadResponseMutableLiveData()
    }

    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return firbaseRepository.getOnProgressChangedLiveData()
    }
}