package com.seavus.arabamisat.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seavus.arabamisat.databinding.ActivityMainCarBinding

class MainCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainCarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}