package com.seavus.arabamisat.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import com.seavus.arabamisat.model.Car

class FirebaseRepository {
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private val reference = FirebaseStorage.getInstance().reference
    private val uploadResponseMutableLiveData: MutableLiveData<Uri> = MutableLiveData()
    private val onProgressChangedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val carsResponseMutableLiveData: MutableLiveData<ArrayList<Car>> = MutableLiveData()

    fun uploadToFirebase(uri: Uri, context: Context) {
        val fileRef = reference.child(
            System.currentTimeMillis().toString() + "." + getFileExtension(uri, context)
        )
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener(fun(uri: Uri) {
                uploadResponseMutableLiveData.value = uri
            })
        }.addOnProgressListener(object :
            OnProgressListener<UploadTask.TaskSnapshot?> {
            override fun onProgress(snapshot: UploadTask.TaskSnapshot) {
                onProgressChangedLiveData.value = true
            }
        }).addOnFailureListener {
            onProgressChangedLiveData.value = false
            Toast.makeText(context, "Uploading Failed !!", Toast.LENGTH_SHORT).show()
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
                var carList: ArrayList<Car> = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val car: Car = dataSnapshot.getValue(Car::class.java)!!
                    carList.add(car)
                }
                carsResponseMutableLiveData.value = carList
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

    fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Car>> {
        return carsResponseMutableLiveData
    }


    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return onProgressChangedLiveData
    }
}