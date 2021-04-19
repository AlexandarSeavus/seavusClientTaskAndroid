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
import com.seavus.arabamisat.util.Base64Handler
import com.seavus.arabamisat.viewmodel.UploadCarViewModel


class UploadCarFragment : Fragment() {
    private var uploadCarFragmentBinding: UploadCarFragmentBinding? = null
    private val getCarsListFragmentBinding get() = uploadCarFragmentBinding!!
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private var imageUri: Uri? = null

    private lateinit var uploadCarViewModel: UploadCarViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        uploadCarFragmentBinding = UploadCarFragmentBinding.inflate(inflater, container, false)
        return getCarsListFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadCarViewModel = ViewModelProvider(this).get(UploadCarViewModel::class.java)

        getCarsListFragmentBinding.imageViewPlaceholder.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        }
        getCarsListFragmentBinding.uploadButton.setOnClickListener {
            getCarsListFragmentBinding.progressBar.setVisibility(View.VISIBLE)
            if (imageUri != null) {
                uploadCarViewModel.uploadToFirebase(imageUri!!, requireContext())
            } else {
                Toast.makeText(
                    activity,
                    "Plese select image!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        uploadCarViewModel.getUploadResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<Uri> {
                override fun onChanged(uri: Uri?) {
                    val modelId = root.push().key
                    val car = Car(
                        modelId!!,
                        uri.toString(),
                        getCarsListFragmentBinding.imageDescriptionEditText.text.toString(),
                        Base64Handler.encodeImage(imageUri, requireContext()),
                        true
                    )
                    root.child(modelId!!).setValue(car)
                    getCarsListFragmentBinding.progressBar.setVisibility(View.INVISIBLE)
                    Toast.makeText(
                        activity,
                        "Uploaded Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            })
        uploadCarViewModel.getOnProgressChangedLiveData()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarsListFragmentBinding.progressBar.setVisibility(View.VISIBLE)
                    } else {
                        getCarsListFragmentBinding.progressBar.setVisibility(View.GONE)
                    }
                }
            })

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            getCarsListFragmentBinding.imageViewPlaceholder.setImageURI(imageUri)
        }
    }
}