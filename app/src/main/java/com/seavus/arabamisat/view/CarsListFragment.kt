package com.seavus.arabamisat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.CarsListFragmentBinding
import com.seavus.arabamisat.model.Car
import com.seavus.arabamisat.view.adapter.CarImagesRecyclerViewAdapter
import com.seavus.arabamisat.viewmodel.UploadCarViewModel


class CarsListFragment : Fragment() {
    private lateinit var carImagesRecyclerViewAdapter: CarImagesRecyclerViewAdapter
    private var carsListFragmentBinding: CarsListFragmentBinding? = null
    private val getCarsListFragmentBinding get() = carsListFragmentBinding!!
    private lateinit var uploadCarViewModel: UploadCarViewModel

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
        uploadCarViewModel.getCars()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadCarViewModel = ViewModelProvider(this).get(UploadCarViewModel::class.java)
        getCarsListFragmentBinding.carsRecyclerView.setHasFixedSize(true)
        getCarsListFragmentBinding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_carsListFragment_to_uploadCarFragment)
        }
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

        uploadCarViewModel.getCarsResponseMutableLiveData()
            .observe(requireActivity(), object : Observer<ArrayList<Car>> {
                override fun onChanged(carsList: ArrayList<Car>?) {
                    if (carsList != null) {
                        carImagesRecyclerViewAdapter =
                            CarImagesRecyclerViewAdapter(requireContext(), carsList)
                        getCarsListFragmentBinding.carsRecyclerView.adapter =
                            carImagesRecyclerViewAdapter
                    }
                }
            })
    }
}