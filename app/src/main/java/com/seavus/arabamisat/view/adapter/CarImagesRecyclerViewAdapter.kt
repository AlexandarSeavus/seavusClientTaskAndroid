package com.seavus.arabamisat.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seavus.arabamisat.databinding.CarItemBinding
import com.seavus.arabamisat.model.Vehicle
import com.squareup.picasso.Picasso

class CarImagesRecyclerViewAdapter(
    val context: Context,
    var vehicleList: List<Vehicle>
) : RecyclerView.Adapter<CarImagesRecyclerViewAdapter.CarViewHolder>() {


    override fun getItemCount(): Int {
        return vehicleList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = CarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = vehicleList[position]
        holder.bindCar(car, position)
    }

    inner class CarViewHolder(private val carItemBinding: CarItemBinding) :
        RecyclerView.ViewHolder(carItemBinding.root) {
        fun bindCar(vehicle: Vehicle, position: Int) {
            Picasso.get().load(vehicle.imagePath).into(carItemBinding.carImageView)
            carItemBinding.carDescription.text = vehicle.description
        }
    }
}
