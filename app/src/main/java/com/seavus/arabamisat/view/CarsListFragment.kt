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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.CarsListFragmentBinding
import com.seavus.arabamisat.model.Car
import com.seavus.arabamisat.util.NetworkChecker
import com.seavus.arabamisat.view.adapter.CarImagesRecyclerViewAdapter
import com.seavus.arabamisat.viewmodel.CarsViewModel
import com.seavus.arabamisat.viewmodel.ViewModelFactory


class CarsListFragment : Fragment() {
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private lateinit var carImagesRecyclerViewAdapter: CarImagesRecyclerViewAdapter
    private var carsListFragmentBinding: CarsListFragmentBinding? = null
    private val getCarsListFragmentBinding get() = carsListFragmentBinding!!
    private lateinit var carsViewModel: CarsViewModel
    private lateinit var unsyncedCarList: List<Car>
    private var inSyncProcess = false
    private var inSyncProcessAddAditionalInfo = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        carsListFragmentBinding = CarsListFragmentBinding.inflate(inflater, container, false)
        return getCarsListFragmentBinding?.root
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
        carsViewModel =
            ViewModelProviders.of(this, activity?.application?.let { ViewModelFactory(it) })
                .get(CarsViewModel::class.java)

        getCarsListFragmentBinding.carsRecyclerView.setHasFixedSize(true)
        getCarsListFragmentBinding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_carsListFragment_to_uploadCarFragment)
        }
        carsViewModel.getOnProgressChangedLiveData()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarsListFragmentBinding.progressBar.visibility = VISIBLE
                        getCarsListFragmentBinding.fab.visibility = GONE
                    } else {
                        getCarsListFragmentBinding.progressBar.visibility = GONE
                        getCarsListFragmentBinding.fab.visibility = VISIBLE
                    }
                }
            })

        carsViewModel.getCarsListVisibilityFBChangedLiveData()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarsListFragmentBinding.carsRecyclerView.visibility = VISIBLE
                    } else {
                        getCarsListFragmentBinding.carsRecyclerView.visibility = GONE
                    }
                }
            })

        carsViewModel.getCarsListVisibilityLDBChangedLiveData()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarsListFragmentBinding.carsRecyclerView.visibility = VISIBLE
                    } else {
                        getCarsListFragmentBinding.carsRecyclerView.visibility = GONE
                    }
                }
            })
        carsViewModel.getSyncProgressChangedLiveData()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarsListFragmentBinding.syncingTxt.visibility = VISIBLE
                        getCarsListFragmentBinding.carsRecyclerView.visibility = GONE
                    } else {
                        getCarsListFragmentBinding.syncingTxt.visibility = GONE
                        getCarsListFragmentBinding.carsRecyclerView.visibility = VISIBLE

                    }
                }
            })

        carsViewModel.getSyncProgressChangedLiveDataFB()
            .observe(requireActivity(), object : Observer<Boolean> {
                override fun onChanged(changed: Boolean) {
                    if (changed) {
                        getCarsListFragmentBinding.syncingTxt.visibility = VISIBLE
                        getCarsListFragmentBinding.carsRecyclerView.visibility = GONE
                    } else {
                        getCarsListFragmentBinding.syncingTxt.visibility = GONE
                        getCarsListFragmentBinding.carsRecyclerView.visibility = VISIBLE
                    }
                }
            })


        carsViewModel.getCarsResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<ArrayList<Car>> {
                override fun onChanged(carsList: ArrayList<Car>?) {
                    if (carsList != null && !carsList.isEmpty()) {
                        carsViewModel.addCarListToLocalDB(carsList)
                        carImagesRecyclerViewAdapter =
                            CarImagesRecyclerViewAdapter(requireContext(), carsList)
                        getCarsListFragmentBinding.carsRecyclerView.adapter =
                            carImagesRecyclerViewAdapter
                    }
                }
            })

        carsViewModel.getLocalDBCarsResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<List<Car>> {
                override fun onChanged(carsList: List<Car>?) {
                    if (carsList != null && !carsList.isEmpty()) {
                        carImagesRecyclerViewAdapter =
                            CarImagesRecyclerViewAdapter(requireContext(), carsList)
                        getCarsListFragmentBinding.carsRecyclerView.adapter =
                            carImagesRecyclerViewAdapter
                    }
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
            .observe(requireActivity(), object : Observer<List<Car>> {
                override fun onChanged(unsyncedCL: List<Car>?) {
                    if (unsyncedCL != null && !unsyncedCL.isEmpty()) {
                        if (!inSyncProcess) {
                            inSyncProcess = true;
                            unsyncedCarList = unsyncedCL
                            for (car in unsyncedCL) {
                                carsViewModel.uploadToFirebase(
                                    Uri.parse(car.imagePath),
                                    requireContext()
                                )
                            }
                        }

                    }
                }
            })

        carsViewModel.getUploadResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<Uri> {
                override fun onChanged(uri: Uri?) {
                    if (!inSyncProcessAddAditionalInfo) {
                        inSyncProcessAddAditionalInfo = true
                        for (car in unsyncedCarList) {
                            car.synced = true
                            car.imagePath = uri.toString()
                            val modelId = root.push().key
                            root.child(modelId!!).setValue(car)
                            return
                        }
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        carsViewModel.clearObservers()
    }

    companion object {
        val TAG = CarsListFragment::class.java.simpleName
    }
}