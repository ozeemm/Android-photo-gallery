package com.example.gallery

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val addButton = findViewById<Button>(R.id.addButton)
        val inputAlbumName = findViewById<EditText>(R.id.inputAlbumName)
        val inputDate = findViewById<EditText>(R.id.inputDate)
        val galleryLinearLayout = findViewById<LinearLayout>(R.id.galleryLinearLayout)

        val photoList = ArrayList<Photo>()

        addButton.setOnClickListener{
            val album = inputAlbumName.text.toString()
            val date = inputDate.text.toString()

            val photo = Photo(album, date)
            photoList.add(photo)

            inputAlbumName.text.clear()
            inputDate.text.clear()

            val photoItem = getNewPhotoItem(photo, galleryLinearLayout)
            galleryLinearLayout.addView(photoItem)
        }
    }

    private fun getNewPhotoItem(photo: Photo, galleryLinearLayout: LinearLayout): View {
        val photoItem = layoutInflater.inflate(R.layout.photo_item, galleryLinearLayout, false)

        val photoImage = photoItem.findViewById<ImageView>(R.id.photoImage)
        val photoAlbumName = photoItem.findViewById<TextView>(R.id.photoAlbumName)
        val photoDate = photoItem.findViewById<TextView>(R.id.photoDate)

        photoAlbumName.text = photo.album
        photoDate.text = photo.date

        return photoItem
    }
}