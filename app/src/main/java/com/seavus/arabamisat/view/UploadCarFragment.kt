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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.UploadCarFragmentBinding
import com.seavus.arabamisat.model.Vehicle
import com.seavus.arabamisat.util.NetworkChecker
import com.seavus.arabamisat.viewmodel.VehicleViewModel

import java.util.*


class UploadCarFragment : Fragment() {
    private lateinit var carsListFragmentBinding: UploadCarFragmentBinding
    private lateinit var root: DatabaseReference
    private lateinit var imageUri: Uri
    private var carUUID = ""
    val carsViewModel by viewModels<VehicleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        carsListFragmentBinding = UploadCarFragmentBinding.inflate(inflater, container, false)
        return carsListFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root = FirebaseDatabase.getInstance().getReference(getString(R.string.image))
        carsListFragmentBinding.imageViewPlaceholder.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        }

        carsListFragmentBinding.uploadButton.setOnClickListener {
            carsListFragmentBinding.progressBar.visibility = View.VISIBLE
            carUUID = UUID.randomUUID().toString()
            val car = Vehicle(
                carUUID,
                imageUri.toString(),
                carsListFragmentBinding.imageDescriptionEditText.text.toString(),
                false
            )
            carsViewModel.addCarToLocalDB(car)
            if (NetworkChecker.isNetworkAvailable(requireActivity())) {
                carsViewModel.uploadToFirebase(imageUri, requireContext())
            } else {
                carsListFragmentBinding.progressBar.visibility = View.INVISIBLE
                findNavController().popBackStack()
            }
        }
        carsViewModel.uploadResponse.observe(viewLifecycleOwner) { uri ->
            val modelId = root.push().key
            val car = Vehicle(
                carUUID,
                uri.toString(),
                carsListFragmentBinding.imageDescriptionEditText.text.toString(),
                true
            )
            root.child(modelId!!).setValue(car)
            carsListFragmentBinding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(
                activity,
                requireContext().getString(R.string.upload_successfully),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }

        carsViewModel.getOnProgressChangedLiveData()
            .observe(requireActivity(), Observer<Boolean> { changed ->
                if (changed) {
                    carsListFragmentBinding.progressBar.visibility = View.VISIBLE
                    carsListFragmentBinding.uploadButton.isEnabled = false
                } else {
                    carsListFragmentBinding.progressBar.visibility = View.GONE
                    carsListFragmentBinding.uploadButton.isEnabled = true
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
            imageUri = data.data!!
            carsListFragmentBinding.imageViewPlaceholder.setImageURI(imageUri)
        }
    }
}