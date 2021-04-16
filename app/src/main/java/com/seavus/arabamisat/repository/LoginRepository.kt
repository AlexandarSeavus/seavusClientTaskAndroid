package com.seavus.arabamisat.repository

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.seavus.arabamisat.view.LoginActivity

class LoginRepository {
    private val authResponseMutableLiveData: MutableLiveData<FirebaseUser> =
        MutableLiveData()

    fun startFacebookLogin(
        loginButton: LoginButton,
        callbackManager: CallbackManager,
        mFirebaseAuth: FirebaseAuth,
        activity: LoginActivity
    ) {
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                result?.accessToken?.let { handleFacebookToken(it, mFirebaseAuth, activity) }
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })
    }

    private fun handleFacebookToken(
        accessToken: AccessToken,
        mFirebaseAuth: FirebaseAuth,
        activity: LoginActivity
    ) {
        var authCredential: AuthCredential =
            FacebookAuthProvider.getCredential(accessToken.token)
        mFirebaseAuth?.signInWithCredential(authCredential)
            ?.addOnCompleteListener(activity, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful) {
                        var firebaseUser: FirebaseUser = mFirebaseAuth!!.currentUser
                        authResponseMutableLiveData.value = firebaseUser
                    } else {
                        task.exception?.message?.let {
                            Log.e(
                                LoginRepository::class.java.getName(),
                                it
                            )
                        }
                    }
                }
            })
    }

    fun getAuthResponseMutableLiveData(): LiveData<FirebaseUser> {
        return authResponseMutableLiveData
    }

    fun startGoogleLogin(data: Intent?, mFirebaseAuth: FirebaseAuth?, activity: LoginActivity) {
        var task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, mFirebaseAuth, activity)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        mFirebaseAuth: FirebaseAuth?,
        activity: LoginActivity
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mFirebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                activity,
                { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val firebaseUser: FirebaseUser = mFirebaseAuth!!.getCurrentUser()
                        authResponseMutableLiveData.value = firebaseUser
                    }
                })
    }
}