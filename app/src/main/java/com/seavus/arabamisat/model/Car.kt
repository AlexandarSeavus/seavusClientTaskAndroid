package com.seavus.arabamisat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars_table")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "car_id") val carID: String = "",
    @ColumnInfo(name = "image_path") val imagePath: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "synced") val synced: Boolean = false
)