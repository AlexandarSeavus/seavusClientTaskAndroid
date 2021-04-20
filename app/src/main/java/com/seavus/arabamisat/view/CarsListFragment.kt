package com.seavus.arabamisat.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.CarsListFragmentBinding
import com.seavus.arabamisat.model.Car
import com.seavus.arabamisat.util.NetworkChecker
import com.seavus.arabamisat.view.adapter.CarImagesRecyclerViewAdapter
import com.seavus.arabamisat.viewmodel.CarsViewModel
import com.seavus.arabamisat.viewmodel.ViewModelFactory


class CarsListFragment : Fragment() {
    private lateinit var carImagesRecyclerViewAdapter: CarImagesRecyclerViewAdapter
    private var carsListFragmentBinding: CarsListFragmentBinding? = null
    private val getCarsListFragmentBinding get() = carsListFragmentBinding!!
    private lateinit var carsViewModel: CarsViewModel

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
                        getCarsListFragmentBinding.progressBar.setVisibility(View.VISIBLE)
                    } else {
                        getCarsListFragmentBinding.progressBar.setVisibility(View.GONE)
                    }
                }
            })

        carsViewModel.getCarsResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<ArrayList<Car>> {
                override fun onChanged(carsList: ArrayList<Car>?) {
                    if (carsList != null) {
                        carsViewModel.deleteCarListToLocalDB()
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
                    if (carsList != null) {
                        Log.i(
                            TAG,
                            "getLocalDBCarsResponseMutableLiveData in localDB: " + carsList.size
                        )
                        carImagesRecyclerViewAdapter =
                            CarImagesRecyclerViewAdapter(requireContext(), carsList)
                        getCarsListFragmentBinding.carsRecyclerView.adapter =
                            carImagesRecyclerViewAdapter
                    }
                }

            })
    }

    companion object {
        val TAG = CarsListFragment::class.java.simpleName
    }
}