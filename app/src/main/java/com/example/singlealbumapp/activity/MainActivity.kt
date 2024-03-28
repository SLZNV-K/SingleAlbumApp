package com.example.singlealbumapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.singlealbumapp.adapter.SongAdapter
import com.example.singlealbumapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = SongAdapter()
        binding.recycler.adapter = adapter
    }
}