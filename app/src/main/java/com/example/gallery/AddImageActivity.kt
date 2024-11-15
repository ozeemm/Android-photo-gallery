package com.example.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddImageActivity : AppCompatActivity()  {

    private lateinit var imageToSaveView: ImageView
    private lateinit var buttonSave: Button
    private lateinit var inputName: EditText
    private lateinit var inputAlbumName: EditText
    private lateinit var inputDate: EditText

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        imageToSaveView = findViewById(R.id.imageToSaveView)
        buttonSave = findViewById(R.id.buttonSave)
        inputName = findViewById(R.id.inputName)
        inputAlbumName = findViewById(R.id.inputAlbumName)
        inputDate = findViewById(R.id.inputDate)

        imageToSaveView.setOnClickListener {
            // Load image on activity
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)

        }

        buttonSave.setOnClickListener {
            if(imageUri == null)
                return@setOnClickListener

            // Check date format from input
            val photo = getPhotoFromInputs()
            if(photo == null) {
                Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save image in storage
            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val imageExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(imageUri!!))
            val fileName = "${getDefaultPhotoName()}.${imageExtension}"

            val path = StorageUtil.saveImage(fileName, bitmap)

            if(path == null)
                Toast.makeText(this, "Ошибка: Фотография не может быть сохранена", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(this, "Фотография сохранена", Toast.LENGTH_SHORT).show()
                println("Saved image to: $path")
            }

            // Close activity with params
            val data = Intent()
            data.putExtra("Photo", photo)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // On Image Loaded
        if(resultCode != RESULT_OK)
            return

        imageUri = data?.data

        println("Loaded: ${imageUri!!}")
        println("Type: ${contentResolver.getType(imageUri!!)}")

        // Show image
        imageToSaveView.setImageURI(imageUri!!)
        showPhotoInfo()
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

    private fun showPhotoInfo(){
        inputName.setText(getDefaultPhotoName())
        inputAlbumName.setText("Без альбома")
        inputDate.setText(getCurrentDateString())
    }

    private fun getPhotoFromInputs(): Photo?{
        if(!isDateStringCorrect(inputDate.text.toString()))
            return null

        val photo = Photo(
            imageUri.toString(),
            inputName.text.toString(),
            inputDate.text.toString(),
            inputAlbumName.text.toString()
        )

        return photo
    }

    private fun getDefaultPhotoName(): String{
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        return "Image_${timestamp}"
    }

    private fun getCurrentDateString(): String{
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH).format(Date())
    }

    private fun isDateStringCorrect(dateString: String): Boolean{
        val pattern = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH)
        try {
            pattern.parse(dateString)
            return true
        } catch(e: Exception){
            return false
        }
    }
}