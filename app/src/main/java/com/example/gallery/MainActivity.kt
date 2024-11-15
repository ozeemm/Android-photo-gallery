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

    private lateinit var galleryLinearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        galleryLinearLayout = findViewById(R.id.galleryLinearLayout)

        val photos = StorageUtil.getPhotos()

        for(photo in photos){
            galleryLinearLayout.addView(getPhotoItemView(photo, galleryLinearLayout))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable Menu
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Open activity from menu
        if(item.itemId == R.id.menuItemAdd){
            val intent = Intent(this, AddImageActivity::class.java)
            startActivityForResult(intent, 1)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK)
            return

        val photo = data!!.getSerializableExtra("Photo") as Photo
        val photoView = getPhotoItemView(photo, galleryLinearLayout)
        galleryLinearLayout.addView(photoView)
    }

    private fun getPhotoItemView(photo: Photo, container: ViewGroup): View {
        val photoItem = layoutInflater.inflate(R.layout.photo_item, container, false)

        val photoImage = photoItem.findViewById<ImageView>(R.id.photoImage)
        val photoAlbumName = photoItem.findViewById<TextView>(R.id.photoAlbumName)
        val photoDate = photoItem.findViewById<TextView>(R.id.photoDate)

        val album = if (photo.album != null) photo.album else "Без альбома"
        val name = if(photo.name != null) photo.name else "Без названия"
        val date = if(photo.date != null) photo.date else "Без даты"

        photoImage.setImageURI(Uri.parse(photo.uri))
        photoAlbumName.text = "$album/$name"
        photoDate.text = date

        return photoItem
    }
}