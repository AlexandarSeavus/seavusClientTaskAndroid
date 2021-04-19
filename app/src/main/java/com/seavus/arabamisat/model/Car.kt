package com.seavus.arabamisat.model

data class Car(
    val id: String = "",
    val imagePath: String = "",
    val description: String = "",
    val imageBase64: String = "",
    val synced: Boolean = false
)