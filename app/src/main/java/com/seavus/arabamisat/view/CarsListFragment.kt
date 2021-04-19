package com.seavus.arabamisat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.CarsListFragmentBinding

class CarsListFragment : Fragment() {
    private var carsListFragmentBinding: CarsListFragmentBinding? = null
    private val getCarsListFragmentBinding get() = carsListFragmentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        carsListFragmentBinding = CarsListFragmentBinding.inflate(inflater, container, false)
        return getCarsListFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCarsListFragmentBinding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_carsListFragment_to_uploadCarFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        carsListFragmentBinding = null
    }
}