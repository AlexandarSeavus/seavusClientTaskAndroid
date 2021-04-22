package com.seavus.arabamisat.repository

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.seavus.arabamisat.db.VehicleDAO
import com.seavus.arabamisat.db.CarsDatabase
import com.seavus.arabamisat.model.Vehicle
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.io.IOException
import java.util.*

class LocalDBRepositoryTest {
    private lateinit var vehicleDao: VehicleDAO
    private lateinit var db: CarsDatabase


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = CarsDatabase.getInstance(context as Application)
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
        vehicleDao.insert(car)
        vehicleDao.userCount(car.carID).test().assertResult(1)
    }

    @Test
    fun addAllCar() {
        var carList = arrayListOf<Vehicle>()
        val carOne = Vehicle().apply {
            carID = UUID.randomUUID().toString()
            imagePath = "TestPath1"
            description = "DescTest1"
            synced = false
        }
        val carTwo = Vehicle().apply {
            carID = UUID.randomUUID().toString()
            imagePath = "TestPath2"
            description = "DescTest2"
            synced = false
        }
        carList.add(carOne)
        carList.add(carTwo)
        vehicleDao.insertAllCars(carList)
        vehicleDao.userCount().test().assertResult(2)
    }

    @Test
    fun deleteAllCar() {
        vehicleDao.delete()
        vehicleDao.userCount().test().assertResult(0)
    }

    @Test
    fun getUnsyncedCars() {
        val car = Vehicle().apply {
            carID = UUID.randomUUID().toString()
            imagePath = "TestPath1"
            description = "DescTest1"
            synced = false
        }
        vehicleDao.insert(car)
        vehicleDao.getUnsyncedCars(false).test().assertResult(arrayListOf())
    }
}