package com.seavus.arabamisat.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seavus.arabamisat.db.CarsDatabase
import com.seavus.arabamisat.model.Car
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers

class LocalDBRepository(application: Application) {
    private val compositeDisposable = CompositeDisposable()
    private var carsDatabase: CarsDatabase
    private val carListResponseMutableLiveData: MutableLiveData<List<Car>> = MutableLiveData()
    private val insertCartResponseMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val onProgressChangedLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        carsDatabase = CarsDatabase.getInstance(application)
    }

    fun addCar(car: Car) {
        var completable: Completable = carsDatabase.carsDAO().insert(car)
        compositeDisposable.add(
            completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        insertCartResponseMutableLiveData.value = true
                    }

                    override fun onError(e: Throwable) {
                    }
                })
        )
    }

    fun addAllCar(carList: List<Car>) {
        var completable: Completable = carsDatabase.carsDAO().insertAllCars(carList)
        compositeDisposable.add(
            completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        insertCartResponseMutableLiveData.value = true
                    }

                    override fun onError(e: Throwable) {
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
                    }

                    override fun onError(e: Throwable) {
                    }
                })
        )
    }

    fun getAllCars() {
        onProgressChangedLiveData.value = true
        var single: Maybe<List<Car>> = carsDatabase.carsDAO().getAllCars()
        compositeDisposable.add(
            single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableMaybeObserver<List<Car>>() {
                    override fun onSuccess(carList: List<Car>) {
                        carListResponseMutableLiveData.value = carList
                    }

                    override fun onError(e: Throwable) {
                        onProgressChangedLiveData.value = false
                    }

                    override fun onComplete() {

                    }
                })
        )
    }

    fun getLocalDBCarsResponseMutableLiveData(): LiveData<List<Car>> {
        return carListResponseMutableLiveData
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