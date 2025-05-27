package com.example.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.databinding.ActivityMainBinding
import com.example.gallery.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, MainFragment())
            .commit()
    }
}