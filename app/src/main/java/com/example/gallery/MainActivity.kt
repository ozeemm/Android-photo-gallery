package com.example.gallery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding = ActivityMainBinding .inflate(getLayoutInflater());
        setContentView(R.layout.activity_main)

        val addButton = findViewById<Button>(R.id.addButton)
        val inputAlbumName = findViewById<EditText>(R.id.inputAlbumName)
        val inputDate = findViewById<EditText>(R.id.inputDate)

        val photoList = ArrayList<PhotoItem>()

        addButton.setOnClickListener{
            val album = inputAlbumName.text.toString()
            val date = inputDate.text.toString()

            photoList.add(PhotoItem(album, date))

            inputAlbumName.text.clear()
            inputDate.text.clear()

            println("Photos (${photoList.count()}):")
            for(item in photoList){
                println(item.album + " " + item.date)
            }
        }
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