package com.seavus.arabamisat.repository

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.seavus.arabamisat.db.VehicleDAO
import com.seavus.arabamisat.db.VehicleDatabase
import com.seavus.arabamisat.model.Vehicle
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.io.IOException
import java.util.*

class LocalDBRepoImplTest {
    private lateinit var vehicleDao: VehicleDAO
    private lateinit var db: VehicleDatabase


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = VehicleDatabase.getInstance(context as Application)
        vehicleDao = db.carsDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    fun addCar() {
        val car = Vehicle().apply {
            carID = UUID.randomUUID().toString()
            imagePath = "TestPath"
            description = "DescTest"
            synced = false
        }
        vehicleDao.insert(car).test().assertComplete()
    }

    @Test
    fun deleteAllCar() {
        vehicleDao.delete().test().assertComplete()
    }

    @Test
    fun getUnsyncedCars() {
        vehicleDao.getUnsyncedCars(false).test().assertComplete()
    }
}