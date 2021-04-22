package com.seavus.arabamisat.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.ActivityLoginBinding
import com.seavus.arabamisat.util.Constants.Companion.RC_SIGN_IN
import com.seavus.arabamisat.viewmodel.LoginViewModel
import com.squareup.picasso.Picasso


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var mFirebaseAuth: FirebaseAuth? = null
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureFirebaseAuth()
        configureFacebookLogin()
        configureGoogleLogin()
        setUpLoginObserver()
    }

    private fun configureFirebaseAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
    }

    private fun configureFacebookLogin() {
        loginViewModel.registerFacebookLoginCallback(
            binding.loginButton,
            callbackManager,
            mFirebaseAuth!!,
            this
        )
    }

    private fun configureGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_id_client))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.googleSignInButton.setOnClickListener {
            var intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun setUpLoginObserver() {
        loginViewModel.getAuthResponseMutableLiveData()
            ?.observe(this,
                Observer<FirebaseUser> { firebaseUser -> firebaseUser?.let { showUI(it) } })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            loginViewModel.handleGoogleLogin(data, mFirebaseAuth, this)
        }
    }

    private fun showUI(firebaseUser: FirebaseUser) {
        binding.userName.text = "WELCOME ${firebaseUser.displayName}"
        binding.loginButton.visibility = View.GONE
        binding.googleSignInButton.visibility = View.GONE
        if (firebaseUser.photoUrl != null) {
            Picasso.get().load(firebaseUser.photoUrl).into(binding.imagePlaceholder);
        }
        Handler(Looper.getMainLooper()).postDelayed({
            FirebaseCrashlytics.getInstance().setUserId(firebaseUser.uid)
            val mIntent = Intent(this@LoginActivity, MainCarActivity::class.java)
            startActivity(mIntent)
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFirebaseAuth != null) {
            mFirebaseAuth!!.signOut()
        }
    }
}
