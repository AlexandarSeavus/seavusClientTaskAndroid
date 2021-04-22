package com.seavus.arabamisat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars_table")
data class Vehicle(
    @PrimaryKey
    @ColumnInfo(name = "car_id") var carID: String = "",
    @ColumnInfo(name = "image_path") var imagePath: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "synced") var synced: Boolean = false
)