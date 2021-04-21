package com.seavus.arabamisat.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.seavus.arabamisat.databinding.UploadCarFragmentBinding
import com.seavus.arabamisat.model.Car
import com.seavus.arabamisat.util.NetworkChecker
import com.seavus.arabamisat.viewmodel.CarsViewModel
import java.util.*


class UploadCarFragment : Fragment() {
    private var uploadCarFragmentBinding: UploadCarFragmentBinding? = null
    private val getCarUploadFragmentBinding get() = uploadCarFragmentBinding!!
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private var imageUri: Uri? = null
    private var carUUID = ""

    private lateinit var carsViewModel: CarsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uploadCarFragmentBinding = UploadCarFragmentBinding.inflate(inflater, container, false)
        return getCarUploadFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carsViewModel = ViewModelProvider(this).get(CarsViewModel::class.java)

        getCarUploadFragmentBinding.imageViewPlaceholder.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        }
        getCarUploadFragmentBinding.uploadButton.setOnClickListener {
            if (imageUri != null) {
                getCarUploadFragmentBinding.progressBar.setVisibility(View.VISIBLE)
                carUUID = UUID.randomUUID().toString();
                val car = Car(
                    carUUID,
                    imageUri.toString(),
                    getCarUploadFragmentBinding.imageDescriptionEditText.text.toString(),
                    false
                )
                carsViewModel.addCarToLocalDB(car)
                if (NetworkChecker.isNetworkAvailable(requireActivity())) {
                    carsViewModel.uploadToFirebase(imageUri!!, requireContext())
                } else {
                    getCarUploadFragmentBinding.progressBar.setVisibility(View.INVISIBLE)
                    findNavController().popBackStack()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Plese select image!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        carsViewModel.getUploadResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<Uri> {
                override fun onChanged(uri: Uri?) {
                    val modelId = root.push().key
                    val car = Car(
                        carUUID,
                        uri.toString(),
                        getCarUploadFragmentBinding.imageDescriptionEditText.text.toString(),
                        true
                    )
                    root.child(modelId!!).setValue(car)
                    getCarUploadFragmentBinding.progressBar.setVisibility(View.INVISIBLE)
                    Toast.makeText(
                        activity,
                        "Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            })
        carsViewModel.getOnProgressChangedLiveData()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarUploadFragmentBinding.progressBar.setVisibility(View.VISIBLE)
                        getCarUploadFragmentBinding.uploadButton.isEnabled = false
                    } else {
                        getCarUploadFragmentBinding.progressBar.setVisibility(View.GONE)
                        getCarUploadFragmentBinding.uploadButton.isEnabled = true
                    }
                }
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        carsViewModel.clearObservers()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            getCarUploadFragmentBinding.imageViewPlaceholder.setImageURI(imageUri)
        }
    }
}