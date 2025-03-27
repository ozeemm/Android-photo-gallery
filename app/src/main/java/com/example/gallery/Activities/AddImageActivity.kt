package com.example.gallery.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.gallery.Adapters.AlbumSpinnerAdapter
import com.example.gallery.App
import com.example.gallery.Enums.AddImageActivityType
import com.example.gallery.Model.Album
import com.example.gallery.Model.Photo
import com.example.gallery.R
import com.example.gallery.Utils.BitmapConverter
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AddImageActivity : AppCompatActivity()  {

    private lateinit var imageToSaveView: ImageView
    private lateinit var buttonSave: Button
    private lateinit var inputName: EditText
    private lateinit var inputAlbumName: EditText
    private lateinit var inputDate: EditText
    private lateinit var spinnerAlbumName: Spinner
    private lateinit var newAlbumCheckbox: CheckBox
    private lateinit var spinnerAdapter: AlbumSpinnerAdapter

    private val albums = ArrayList<Album>()

    private lateinit var type: AddImageActivityType
    private var isImageLoaded = false
    private var photoToEdit: Photo? = null

    private lateinit var chooseImageLauncher: ActivityResultLauncher<Intent>

    private val randomImagesUrl = "https://picsum.photos/800/600"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        imageToSaveView = findViewById(R.id.imageToSaveView)
        buttonSave = findViewById(R.id.buttonSave)
        inputName = findViewById(R.id.inputName)
        inputAlbumName = findViewById(R.id.inputAlbumName)
        inputDate = findViewById(R.id.inputDate)
        spinnerAlbumName = findViewById(R.id.spinnerAlbumName)
        newAlbumCheckbox = findViewById(R.id.newAlbumNameCheckBox)

        spinnerAdapter = AlbumSpinnerAdapter(this, android.R.layout.simple_spinner_item, albums)
        spinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinnerAlbumName.adapter = spinnerAdapter
        App.database.albumDao().getAlbums().observe(this){ list ->
            albums.clear()
            albums.addAll(list)
            spinnerAdapter.notifyDataSetChanged()

            if(type == AddImageActivityType.update) {
                showPhotoInfo(photoToEdit!!) // FIX
            }
        }

        type = AddImageActivityType.valueOf(intent.getStringExtra("type")!!)
        if(type == AddImageActivityType.update) {
            photoToEdit = intent.getSerializableExtra("photo", Photo::class.java)!!
            isImageLoaded = true
            showPhotoInfo(photoToEdit!!)
        }

        newAlbumCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                inputAlbumName.visibility = View.VISIBLE
                spinnerAlbumName.visibility = View.INVISIBLE
            }
            else{
                inputAlbumName.visibility = View.INVISIBLE
                spinnerAlbumName.visibility = View.VISIBLE
            }
        }

        imageToSaveView.setOnClickListener {
            if(type != AddImageActivityType.create)
                return@setOnClickListener

            // Load image on activity
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            chooseImageLauncher.launch(Intent.createChooser(intent, "Select Image"))
        }

        chooseImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK)
                return@registerForActivityResult

            val imageUri = result.data!!.data
            println("Loaded: ${imageUri!!}")

            // Show image
            imageToSaveView.setImageURI(imageUri)
            isImageLoaded = true
            showPhotoInfo()
        }

        buttonSave.setOnClickListener {
            when(type){
                AddImageActivityType.create -> saveImage()
                AddImageActivityType.update -> editImage(photoToEdit!!)
            }
        }
    }

    private fun saveImage(){
        if(!isImageLoaded)
            return

        // Check date format from input
        if(!isDateStringCorrect(inputDate.text.toString())) {
            Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = imageToSaveView.drawable.toBitmap()

        val photo = Photo(
            inputName.text.toString(),
            inputDate.text.toString(),
            BitmapConverter.bitmapToString(bitmap)
        )

        finishActivity(photo)
    }

    private fun loadRandomBitmap(): Deferred<Bitmap?> {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(randomImagesUrl)
                    .get()
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val bitmap = BitmapFactory.decodeStream(response.body?.byteStream())
                    return@async bitmap
                } else {
                    throw IOException()
                }
            } catch (e: IOException) {
                throw IOException()
            }
        }
    }

    private fun editImage(photo: Photo){
        if(!isDateStringCorrect(inputDate.text.toString())) {
            Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
            return
        }

        photo.name = inputName.text.toString()
        photo.date = inputDate.text.toString()

        Toast.makeText(this, "Фотография изменена", Toast.LENGTH_SHORT).show()
        finishActivity(photo)
    }

    private fun finishActivity(photo: Photo){
        val data = Intent()
        data.putExtra("photo", photo)

        if(newAlbumCheckbox.isChecked){
            data.putExtra("is_new_album", true)
            data.putExtra("new_album", inputAlbumName.text.toString())
        } else{
            data.putExtra("is_new_album", false)
            val selectedAlbum: Album = spinnerAlbumName.selectedItem as Album
            photo.albumId = selectedAlbum.id
        }

        try {
            setResult(Activity.RESULT_OK, data)
            finish()
        } catch(e: Exception){
            Toast.makeText(this, "Ошибка: фотография слишком большая", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable back arrow button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Enable menu
        if(type == AddImageActivityType.create) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.add_image_menu, menu)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Close on back arrow click
        if(item.itemId == android.R.id.home){
            finish()
        }
        if(item.itemId == R.id.menuRandomImage){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    val bitmap = loadRandomBitmap().await()
                    runOnUiThread {
                        imageToSaveView.setImageBitmap(bitmap)
                        isImageLoaded = true
                        showPhotoInfo()
                    }
                }
                catch(e: IOException){
                    runOnUiThread {
                        Toast.makeText(this@AddImageActivity, "Не удалось загрузить случайную картинку", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showPhotoInfo(){
        inputName.setText(getDefaultPhotoName())
        inputDate.setText(getCurrentDateString())
    }

    private fun showPhotoInfo(photo: Photo){
        imageToSaveView.setImageBitmap(photo.bitmap)
        inputName.setText(photo.name)
        spinnerAlbumName.setSelection(spinnerAdapter.getPosition(photo.album!!))
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