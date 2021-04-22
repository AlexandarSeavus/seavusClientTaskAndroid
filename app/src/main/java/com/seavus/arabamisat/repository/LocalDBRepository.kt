package com.seavus.arabamisat.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.seavus.arabamisat.R
import com.seavus.arabamisat.db.CarsDatabase
import com.seavus.arabamisat.model.Vehicle
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers

class LocalDBRepository(var application: Application) {
    private val compositeDisposable = CompositeDisposable()
    private var carsDatabase: CarsDatabase
    private val vehicleListResponseMutableLiveData: MutableLiveData<List<Vehicle>> =
        MutableLiveData()
    private val insertCartResponseMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val onProgressChangedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val unsyncedCarsResponseMutableLiveData: MutableLiveData<List<Vehicle>> =
        MutableLiveData()

    init {
        carsDatabase = CarsDatabase.getInstance(application)
    }

    fun addCar(vehicle: Vehicle) {
        var completable: Completable = carsDatabase.carsDAO().insert(vehicle)
        compositeDisposable.add(
            completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        insertCartResponseMutableLiveData.value = true
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey(application.getString(R.string.db_key), "addCar")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        )
    }

    fun addAllCar(vehicleList: List<Vehicle>) {
        var completable: Completable = carsDatabase.carsDAO().insertAllCars(vehicleList)
        compositeDisposable.add(
            completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        insertCartResponseMutableLiveData.value = true
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey(application.getString(R.string.db_key), "addAllCar")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        )
    }

    fun deleteAllCar() {
        var completable: Completable = carsDatabase.carsDAO().delete()
        compositeDisposable.add(
            completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        // No need for now
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey(application.getString(R.string.db_key), "deleteAllCar")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                })
        )
    }

    fun getAllCars() {
        onProgressChangedLiveData.value = true
        var single: Maybe<List<Vehicle>> = carsDatabase.carsDAO().getAllCars()
        compositeDisposable.add(
            single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableMaybeObserver<List<Vehicle>>() {
                    override fun onSuccess(vehicleList: List<Vehicle>) {
                        vehicleListResponseMutableLiveData.value = vehicleList
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey(application.getString(R.string.db_key), "getAllCars")
                        FirebaseCrashlytics.getInstance().recordException(e)
                        onProgressChangedLiveData.value = false
                    }

                    override fun onComplete() {

                    }
                })
        )
    }

    fun getUnsyncedCars() {
        var maybe: Maybe<List<Vehicle>> = carsDatabase.carsDAO().getUnsyncedCars(false)
        compositeDisposable.add(
            maybe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableMaybeObserver<List<Vehicle>>() {
                    override fun onSuccess(cl: List<Vehicle>) {
                        unsyncedCarsResponseMutableLiveData.value = cl
                    }

                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        onProgressChangedLiveData.value = false
                    }

                })
        )
    }

    fun getLocalDBCarsResponseMutableLiveData(): LiveData<List<Vehicle>> {
        return vehicleListResponseMutableLiveData
    }

    fun getUnsyncedCarsResponseMutableLiveData(): LiveData<List<Vehicle>> {
        return unsyncedCarsResponseMutableLiveData
    }


    fun getOnProgressChangedLiveData(): LiveData<Boolean> {
        return onProgressChangedLiveData
    }

    fun getInsertCartLocalDBResponseMutableLiveData(): LiveData<Boolean> {
        return insertCartResponseMutableLiveData
    }


    fun clearDisposable() {
        compositeDisposable.clear()
    }
}