package com.seavus.arabamisat.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seavus.arabamisat.databinding.CarItemBinding
import com.seavus.arabamisat.model.Car
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class CarImagesRecyclerViewAdapter(
    val context: Context,
    var carList: List<Car>
) : RecyclerView.Adapter<CarImagesRecyclerViewAdapter.CarViewHolder>() {


    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = CarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = carList[position]
        holder.bindCar(car, position)
    }

    inner class CarViewHolder(private val carItemBinding: CarItemBinding) :
        RecyclerView.ViewHolder(carItemBinding.root) {
        fun bindCar(car: Car, position: Int) {
            Picasso.get().load(car.imagePath).into(carItemBinding.carImageView)
            carItemBinding.carDescription.text = car.description
        }
    }
}
