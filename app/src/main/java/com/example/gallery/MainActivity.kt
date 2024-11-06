package com.example.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val galleryLinearLayout = findViewById<LinearLayout>(R.id.galleryLinearLayout)

        val imagesUri = StorageUtil.getImagesUri()
        println("Found ${imagesUri.size} images")

        for(imageUri in imagesUri){
            galleryLinearLayout.addView(getPhotoItemView(imageUri, galleryLinearLayout))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menuItemAdd){
            val intent = Intent(this, AddImageActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getPhotoItemView(uriStr: String, container: ViewGroup): View {
        val photoItem = layoutInflater.inflate(R.layout.photo_item, container, false)

        val photoImage = photoItem.findViewById<ImageView>(R.id.photoImage)
        val photoAlbumName = photoItem.findViewById<TextView>(R.id.photoAlbumName)
        val photoDate = photoItem.findViewById<TextView>(R.id.photoDate)

        photoImage.setImageURI(Uri.parse(uriStr))
        photoAlbumName.text = "Я альбом"
        photoDate.text = "Я дата"

        return photoItem
    }
}