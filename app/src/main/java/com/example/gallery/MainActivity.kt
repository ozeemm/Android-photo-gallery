package com.example.gallery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding .inflate(getLayoutInflater());
        setContentView(R.layout.activity_main)

        /*
        val display: TextView = findViewById(R.id.text_display)
        val button: Button = findViewById(R.id.button)
        val input: EditText = findViewById(R.id.text_input)

        button.setOnClickListener(){
            display.text = input.text
            input.setText("")
        }
         */
    }
}