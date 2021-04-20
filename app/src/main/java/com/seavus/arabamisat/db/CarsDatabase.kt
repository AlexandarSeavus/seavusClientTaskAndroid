package com.seavus.arabamisat.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.seavus.arabamisat.model.Car

@Database(entities = [Car::class], version = 1,  exportSchema = false)
abstract class CarsDatabase : RoomDatabase() {
    abstract fun carsDAO(): CarDAO

    companion object {
        @Volatile
        private lateinit var INSTANCE: CarsDatabase
        fun getInstance(application: Application): CarsDatabase {
            synchronized(CarsDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        application,
                        CarsDatabase::class.java,
                        "cars_database"
                    ).addCallback(rDC).fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE;
            }
        }

        var rDC: Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }

    }

}