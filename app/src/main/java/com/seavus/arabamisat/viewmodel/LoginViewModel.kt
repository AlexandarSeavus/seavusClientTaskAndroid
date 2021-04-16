package com.seavus.arabamisat.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.seavus.arabamisat.repository.LoginRepository
import com.seavus.arabamisat.view.LoginActivity

class LoginViewModel : ViewModel() {
    private var loginRepository: LoginRepository

    init {
        loginRepository = LoginRepository()
    }

    fun registerFacebookLoginCallback(
        loginButton: LoginButton,
        callbackManager: CallbackManager,
        mFirebaseAuth: FirebaseAuth,
        activity: LoginActivity
    ) {
        loginRepository.startFacebookLogin(loginButton, callbackManager, mFirebaseAuth, activity)
    }

    fun getAuthResponseMutableLiveData(): LiveData<FirebaseUser> {
        return loginRepository.getAuthResponseMutableLiveData()
    }

    fun handleGoogleLogin(data: Intent?, mFirebaseAuth: FirebaseAuth?, activity: LoginActivity) {
        loginRepository.startGoogleLogin(data, mFirebaseAuth, activity)
    }

}