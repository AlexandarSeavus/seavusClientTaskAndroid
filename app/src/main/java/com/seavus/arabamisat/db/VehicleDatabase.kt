package com.seavus.arabamisat.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.seavus.arabamisat.model.Vehicle

@Database(entities = [Vehicle::class], version = 1, exportSchema = false)
abstract class VehicleDatabase : RoomDatabase() {
    abstract fun carsDAO(): VehicleDAO

    companion object {
        @Volatile
        private lateinit var INSTANCE: VehicleDatabase
        fun getInstance(application: Application): VehicleDatabase {
            synchronized(VehicleDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        application,
                        VehicleDatabase::class.java,
                        "cars_database"
                    ).addCallback(rDC).fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE
            }
        }

        var rDC: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }

    }

}