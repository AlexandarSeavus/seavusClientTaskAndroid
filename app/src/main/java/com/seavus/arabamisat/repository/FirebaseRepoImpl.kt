package com.seavus.arabamisat.repository

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.seavus.arabamisat.R
import com.seavus.arabamisat.model.Vehicle
import com.seavus.arabamisat.util.getFileExtension
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Singleton
class FirebaseRepoImpl(var application: Application) : IFirebaseRepo {
    private val root: DatabaseReference = FirebaseDatabase.getInstance().getReference(application.getString(R.string.image))
    private val reference = FirebaseStorage.getInstance().reference
    private val onProgressChangedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val carsResponseMutableLiveData: MutableLiveData<ArrayList<Vehicle>> = MutableLiveData()
    var vehicleList: ArrayList<Vehicle> = arrayListOf()

    override suspend fun uploadToFirebase(uri: Uri, context: Context): Uri =
        suspendCoroutine { cont ->
            val fileRef = reference.child(
                System.currentTimeMillis().toString() + "." + uri.getFileExtension(context)
            )
            fileRef.putFile(uri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener(fun(uri: Uri) {
                    cont.resume(uri)
                })
            }.addOnProgressListener { onProgressChangedLiveData.value = true }
                .addOnFailureListener {
                    FirebaseCrashlytics.getInstance()
                        .setCustomKey(context.getString(R.string.firebase_key), "uploadToFirebase")
                    FirebaseCrashlytics.getInstance().recordException(it)
                    onProgressChangedLiveData.value = false
                    Toast.makeText(
                        context,
                        context.getString(R.string.upload_failed),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }

    override fun getCars() {
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

    override fun getCarsResponseMutableLiveData(): LiveData<ArrayList<Vehicle>> {
        return carsResponseMutableLiveData
    }


    override fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return onProgressChangedLiveData
    }

}