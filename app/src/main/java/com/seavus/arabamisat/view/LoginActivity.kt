package com.seavus.arabamisat.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.seavus.arabamisat.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mFirebaseAuth: FirebaseAuth? = null
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mFirebaseAuth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.registerCallback(
            callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.accessToken?.let { handleFacebookToken(it) }
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                }
            })
    }

    private fun handleFacebookToken(accessToken: AccessToken) {
        var authCredential: AuthCredential = FacebookAuthProvider.getCredential(accessToken.token)
        mFirebaseAuth?.signInWithCredential(authCredential)
            ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful) {
                        var firebaseUser: FirebaseUser = mFirebaseAuth!!.currentUser
                        showUI(firebaseUser)
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showUI(firebaseUser: FirebaseUser) {
        if (firebaseUser != null) {
            binding.userName.text = "WELCOME ${firebaseUser.displayName}"
            binding.loginButton.visibility = View.GONE
        }
        if (firebaseUser.photoUrl != null) {
            Picasso.get().load(firebaseUser.photoUrl).into(binding.imagePlaceholder);
        }
    }
}
