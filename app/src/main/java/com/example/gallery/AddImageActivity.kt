package com.example.gallery

import android.content.Intent
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddImageActivity : AppCompatActivity()  {

    lateinit var imageToSaveView: ImageView
    lateinit var buttonSave: Button

    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        imageToSaveView = findViewById<ImageView>(R.id.imageToSaveView)
        buttonSave = findViewById<Button>(R.id.buttonSave)

        imageToSaveView.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)

        }

        buttonSave.setOnClickListener {
            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
            val imageExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(imageUri!!))
            val fileName = "image_${timeStamp}.${imageExtension}"

            val path = StorageUtil.saveImage(fileName, bitmap)

            if(path == null)
                Toast.makeText(this, "ERROR: Image not saved", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
            println("Saved image to: ${path}")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK && resultCode != 100)
            return

        imageUri = data?.data
        println("Loaded: ${imageUri.toString()}")
        println("Type: ${contentResolver.getType(imageUri!!)}")

        var exifDate: LocalDateTime? = null
        contentResolver.openInputStream(imageUri!!)?.use { stream ->
            val exif = ExifInterface(stream)
            // orig: 2024:08:24 11:35:16
            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, "01:01:2013 12:34:56")
            //exif.saveAttributes()

            val exifDateFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
            var exifDateString = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)

            println("1) $exifDateString")
            if (exifDateString == null) {
                exifDateString = exif.getAttribute(ExifInterface.TAG_DATETIME)
                println("2) $exifDateString")
            }
        }

        if(imageUri != null){
            imageToSaveView.setImageURI(imageUri)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable back arrow button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Close on back arrow click
        if(item.itemId == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}