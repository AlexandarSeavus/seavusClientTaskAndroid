package com.seavus.arabamisat.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.seavus.arabamisat.model.Vehicle

interface IFirebaseRepo {
    suspend fun uploadToFirebase(uri: Uri, context: Context): Uri
    fun getCars()
    fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Vehicle>>
    fun getOnProgressChangedLiveData(): LiveData<Boolean>
}