package com.seavus.arabamisat.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.CarsListFragmentBinding
import com.seavus.arabamisat.model.Vehicle
import com.seavus.arabamisat.util.NetworkChecker
import com.seavus.arabamisat.view.adapter.CarImagesRecyclerViewAdapter
import com.seavus.arabamisat.viewmodel.CarsViewModel


class CarsListFragment : Fragment() {
    private lateinit var root: DatabaseReference
    private lateinit var carImagesRecyclerViewAdapter: CarImagesRecyclerViewAdapter
    private lateinit var carsListFragmentBinding: CarsListFragmentBinding
    private  var unsyncedVehicleList: List<Vehicle> = arrayListOf()
    private var inSyncProcess = false
    private var inSyncProcessAddAditionalInfo = false
    val carsViewModel by viewModels<CarsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        carsListFragmentBinding = CarsListFragmentBinding.inflate(inflater, container, false)
        return carsListFragmentBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (NetworkChecker.isNetworkAvailable(requireActivity())) {
            carsViewModel.getCars()
        } else {
            carsViewModel.getCarFromLocalDB()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root = FirebaseDatabase.getInstance().getReference(getString(R.string.image))
        carsListFragmentBinding.carsRecyclerView.setHasFixedSize(true)
        carsListFragmentBinding.fab.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.uploadCarFragment)
        }

        carsViewModel.getOnProgressChangedLiveData()
            .observe(requireActivity(), Observer<Boolean> { changed ->
                if (changed) {
                    carsListFragmentBinding.progressBar.visibility = VISIBLE
                    carsListFragmentBinding.fab.visibility = GONE
                } else {
                    carsListFragmentBinding.progressBar.visibility = GONE
                    carsListFragmentBinding.fab.visibility = VISIBLE
                }
            })

        carsViewModel.getCarsResponseMutableLiveData()
            .observe(requireActivity(),
                Observer<ArrayList<Vehicle>> { carsList ->
                    if (!carsList.isEmpty()) {
                        carsViewModel.addCarListToLocalDB(carsList)
                        carImagesRecyclerViewAdapter =
                            CarImagesRecyclerViewAdapter(requireContext(), carsList)
                        carsListFragmentBinding.carsRecyclerView.adapter =
                            carImagesRecyclerViewAdapter
                    }
                })

        carsViewModel.getLocalDBCarsResponseMutableLiveData()
            .observe(requireActivity(),
                Observer<List<Vehicle>> { carsList ->
                    if (!carsList.isEmpty()) {
                        carImagesRecyclerViewAdapter =
                            CarImagesRecyclerViewAdapter(requireContext(), carsList)
                        carsListFragmentBinding.carsRecyclerView.adapter =
                            carImagesRecyclerViewAdapter
                    }
                })
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        carsViewModel.getUnsyncedCars()
                    }

                    override fun onLost(network: Network) {
                        inSyncProcess = false
                        inSyncProcessAddAditionalInfo = false
                    }
                })
            }
        }
        setUpSyncingListeners()
    }

    private fun setUpSyncingListeners() {
        carsViewModel.getUnsyncedCarsResponseMutableLiveData()
            .observe(requireActivity(),
                Observer<List<Vehicle>> { unsyncedCL ->
                    if (!unsyncedCL.isEmpty()) {
                        if (!inSyncProcess) {
                            inSyncProcess = true;
                            unsyncedVehicleList = unsyncedCL
                            for (car in unsyncedCL) {
                                carsViewModel.uploadToFirebase(
                                    Uri.parse(car.imagePath),
                                    requireContext()
                                )
                            }
                        }

                    }
                })

        carsViewModel.getUploadResponseMutableLiveData()
            .observe(requireActivity(), Observer<Uri> { uri ->
                if (!inSyncProcessAddAditionalInfo) {
                    inSyncProcessAddAditionalInfo = true
                    for (car in unsyncedVehicleList) {
                        car.synced = true
                        car.imagePath = uri.toString()
                        val modelId = root.push().key
                        root.child(modelId!!).setValue(car)
                        return@Observer
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        carsViewModel.clearObservers()
    }
}