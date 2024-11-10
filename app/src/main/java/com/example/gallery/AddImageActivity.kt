package com.example.gallery

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
import java.time.LocalDateTime
import java.util.*

class AddImageActivity : AppCompatActivity()  {

    lateinit var imageToSaveView: ImageView
    lateinit var buttonSave: Button
    lateinit var inputName: EditText
    lateinit var inputAlbumName: EditText
    lateinit var inputDate: EditText

    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        imageToSaveView = findViewById<ImageView>(R.id.imageToSaveView)
        buttonSave = findViewById<Button>(R.id.buttonSave)
        inputName = findViewById<EditText>(R.id.inputName)
        inputAlbumName = findViewById<EditText>(R.id.inputAlbumName)
        inputDate = findViewById<EditText>(R.id.inputDate)

        imageToSaveView.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)

        }

        buttonSave.setOnClickListener {
            if(imageUri == null)
                return@setOnClickListener

            val photo = getPhotoFromInputs()
            if(photo != null) {
                println(photo.uri)
                println(photo.name)
                println(photo.date)
                println(photo.album)
            }
            else{
                Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val imageExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(imageUri!!))
            val fileName = "${getDefaultPhotoName()}.${imageExtension}"

            val path = StorageUtil.saveImage(fileName, bitmap)

            if(path == null)
                Toast.makeText(this, "Ошибка: Фотография не может быть сохранена", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(this, "Фотография сохранена", Toast.LENGTH_SHORT).show()
                println("Saved image to: ${path}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK && resultCode != 100)
            return

        imageUri = data?.data

        println("Loaded: ${imageUri!!}")
        println("Type: ${contentResolver.getType(imageUri!!)}")

        imageToSaveView.setImageURI(imageUri!!)
        showPhotoInfo()
    }

    private fun showPhotoInfo(){
        inputName.setText(getDefaultPhotoName())
        inputAlbumName.setText("Без альбома")
        inputDate.setText(getCurrentDateString())
    }

    private fun getPhotoFromInputs(): Photo?{
        if(!isDateStringCorrect(inputDate.text.toString()))
            return null

        val photo = Photo()
        photo.uri = imageUri.toString()
        photo.name = inputName.text.toString()
        photo.album = inputAlbumName.text.toString()
        photo.date = inputDate.text.toString()

        return photo
    }

    private fun getDefaultPhotoName(): String{
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        return "Image_${timestamp}"
    }

    private fun getCurrentDateString(): String{
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH).format(Date())
    }

    private fun isDateStringCorrect(dateStr: String): Boolean{
        val pattern = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH)
        try {
            val dateTime = pattern.parse(dateStr)
            return true
        } catch(e: Exception){
            return false
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