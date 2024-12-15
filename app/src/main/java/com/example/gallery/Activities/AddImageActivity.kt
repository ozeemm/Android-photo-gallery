package com.example.gallery.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.Model.Photo
import com.example.gallery.R
import com.example.gallery.Utils.BitmapConverter
import java.util.*

class AddImageActivity : AppCompatActivity()  {

    private lateinit var imageToSaveView: ImageView
    private lateinit var buttonSave: Button
    private lateinit var inputName: EditText
    private lateinit var inputAlbumName: EditText
    private lateinit var inputDate: EditText

    private var imageUri: Uri? = null

    private lateinit var type: String
    private var photoToEdit: Photo? = null

    private lateinit var chooseImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        imageToSaveView = findViewById(R.id.imageToSaveView)
        buttonSave = findViewById(R.id.buttonSave)
        inputName = findViewById(R.id.inputName)
        inputAlbumName = findViewById(R.id.inputAlbumName)
        inputDate = findViewById(R.id.inputDate)

        type = intent.getStringExtra("type")!!
        if(type == "update") {
            photoToEdit = intent.getSerializableExtra("photo", Photo::class.java)!!
            showPhotoInfo(photoToEdit!!)
        }

        chooseImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK)
                return@registerForActivityResult

            imageUri = result.data!!.data

            println("Loaded: ${imageUri!!}")
            println("Type: ${contentResolver.getType(imageUri!!)}")

            // Show image
            imageToSaveView.setImageURI(imageUri!!)
            showPhotoInfo()
        }

        imageToSaveView.setOnClickListener {
            if(type != "create")
                return@setOnClickListener

            // Load image on activity
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            chooseImageLauncher.launch(Intent.createChooser(intent, "Select Image"))
        }

        buttonSave.setOnClickListener {
            when(type){
                "create" -> saveImage()
                "update" -> editImage(photoToEdit!!)
            }
        }
    }

    private fun saveImage(){
        if(imageUri == null)
            return

        // Check date format from input
        if(!isDateStringCorrect(inputDate.text.toString())) {
            Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        val source = ImageDecoder.createSource(contentResolver, imageUri!!)
        val bitmap = ImageDecoder.decodeBitmap(source)

        val photo = Photo(
            inputName.text.toString(),
            inputDate.text.toString(),
            inputAlbumName.text.toString(),
            BitmapConverter.bitmapToString(bitmap)
        )

        finishActivity(photo)
    }

    private fun editImage(photo: Photo){
        if(!isDateStringCorrect(inputDate.text.toString())) {
            Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        photo.name = inputName.text.toString()
        photo.date = inputDate.text.toString()
        photo.album = inputAlbumName.text.toString()

        Toast.makeText(this, "Фотография изменена", Toast.LENGTH_SHORT).show()
        finishActivity(photo)
    }

    private fun finishActivity(photo: Photo){
        val data = Intent()
        data.putExtra("photo", photo)
        setResult(Activity.RESULT_OK, data)
        finish()
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

    private fun showPhotoInfo(photo: Photo){
        imageToSaveView.setImageBitmap(photo.bitmap)
        inputName.setText(photo.name)
        inputAlbumName.setText(photo.album)
        inputDate.setText(photo.date)
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