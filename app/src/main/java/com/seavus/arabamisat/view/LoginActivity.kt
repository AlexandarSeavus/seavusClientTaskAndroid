package com.seavus.arabamisat.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.seavus.arabamisat.R
import com.seavus.arabamisat.databinding.ActivityLoginBinding
import com.seavus.arabamisat.viewmodel.LoginViewModel
import com.squareup.picasso.Picasso


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var mFirebaseAuth: FirebaseAuth? = null
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
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
        binding.googleSignInButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var intent = googleSignInClient.signInIntent
                startActivityForResult(intent, RC_SIGN_IN)
            }
        })
    }

    private fun setUpLoginObserver() {
        loginViewModel.getAuthResponseMutableLiveData()
            ?.observe(this, object : Observer<FirebaseUser> {
                override fun onChanged(firebaseUser: FirebaseUser?) {
                    firebaseUser?.let { showUI(it) }
                }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            loginViewModel.handleGoogleLogin(data, mFirebaseAuth, this)
        }
    }

    private fun showUI(firebaseUser: FirebaseUser) {
        if (firebaseUser == null) return
        if (firebaseUser != null) {
            binding.userName.text = "WELCOME ${firebaseUser.displayName}"
            binding.loginButton.visibility = View.GONE
            binding.googleSignInButton.visibility = View.GONE
        }
        if (firebaseUser.photoUrl != null) {
            Picasso.get().load(firebaseUser.photoUrl).into(binding.imagePlaceholder);
        }
        Handler().postDelayed({
            val mIntent = Intent(this@LoginActivity, MainCarActivity::class.java)
            startActivity(mIntent)
            finish()
        }, 2000)
    }

    companion object {
        val RC_SIGN_IN = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFirebaseAuth != null) {
            mFirebaseAuth!!.signOut()
        }
    }
}
