package com.seavus.arabamisat.repository

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.seavus.arabamisat.R
import com.seavus.arabamisat.model.Vehicle

class FirebaseRepository(var application: Application) {
    private val root: DatabaseReference
    private val reference = FirebaseStorage.getInstance().reference
    private val uploadResponseMutableLiveData: MutableLiveData<Uri> = MutableLiveData()
    private val onProgressChangedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val carsResponseMutableLiveData: MutableLiveData<ArrayList<Vehicle>> = MutableLiveData()
    var vehicleList: ArrayList<Vehicle> = arrayListOf()

    init {
        root = FirebaseDatabase.getInstance().getReference(application.getString(R.string.image))
    }

    fun uploadToFirebase(uri: Uri, context: Context) {
        val fileRef = reference.child(
            System.currentTimeMillis().toString() + "." + getFileExtension(uri, context)
        )
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener(fun(uri: Uri) {
                uploadResponseMutableLiveData.value = uri
            })
        }.addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?> {
            override fun onProgress(snapshot: UploadTask.TaskSnapshot) {
                onProgressChangedLiveData.value = true
            }
        }).addOnFailureListener {
            FirebaseCrashlytics.getInstance()
                .setCustomKey(context.getString(R.string.firebase_key), "uploadToFirebase")
            FirebaseCrashlytics.getInstance().recordException(it)
            onProgressChangedLiveData.value = false
            Toast.makeText(context, context.getString(R.string.upload_failed), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getFileExtension(mUri: Uri, context: Context): String? {
        val cr: ContentResolver = context.getContentResolver()
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }

    fun getCars() {
        onProgressChangedLiveData.value = true
        root.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val vehicle: Vehicle = dataSnapshot.getValue(Vehicle::class.java)!!
                    if (!vehicleList.contains(vehicle)) {
                        vehicleList.add(vehicle)
                    }
                }
                carsResponseMutableLiveData.value = vehicleList
                onProgressChangedLiveData.value = false
            }

            override fun onCancelled(@NonNull error: DatabaseError) {
                onProgressChangedLiveData.value = false
            }
        })
    }

    fun getUploadResponseMutableLiveData(): LiveData<Uri> {
        return uploadResponseMutableLiveData
    }

    fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Vehicle>> {
        return carsResponseMutableLiveData
    }


    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return onProgressChangedLiveData
    }
    
}