package com.example.gallery

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                println(item.id.toString() + " " + item.album + " " + item.date)
            }
        }
    }
}