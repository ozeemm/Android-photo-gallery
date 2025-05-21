package com.example.gallery.Activities

import android.app.Activity
import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.collections.ArrayList

import com.example.gallery.Adapters.AlbumSpinnerAdapter
import com.example.gallery.R
import com.example.gallery.Utils.BitmapConverter
import com.example.gallery.ViewModels.AddImageViewModel

class AddImageActivity : AppCompatActivity()  {

    private lateinit var viewModel: AddImageViewModel

    private lateinit var imageToSaveView: ImageView
    private lateinit var buttonSave: Button
    private lateinit var inputName: EditText
    private lateinit var inputAlbumName: EditText
    private lateinit var inputDate: EditText
    private lateinit var spinnerAlbumName: Spinner
    private lateinit var newAlbumCheckbox: CheckBox
    private lateinit var spinnerAdapter: AlbumSpinnerAdapter

    private lateinit var type: AddImageActivityType

    private lateinit var chooseImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)
        viewModel = ViewModelProvider(this).get(AddImageViewModel::class.java)

        imageToSaveView = findViewById(R.id.imageToSaveView)
        buttonSave = findViewById(R.id.buttonSave)
        inputName = findViewById(R.id.inputName)
        inputAlbumName = findViewById(R.id.inputAlbumName)
        inputDate = findViewById(R.id.inputDate)
        spinnerAlbumName = findViewById(R.id.spinnerAlbumName)
        newAlbumCheckbox = findViewById(R.id.newAlbumNameCheckBox)

        spinnerAdapter = AlbumSpinnerAdapter(this, android.R.layout.simple_spinner_item, ArrayList(emptyList()))
        spinnerAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spinnerAlbumName.adapter = spinnerAdapter

        type = AddImageActivityType.valueOf(intent.getStringExtra("type")!!)

        CoroutineScope(Dispatchers.IO).launch {
            val albumNames = viewModel.getAlbumsNames()

            runOnUiThread{
                spinnerAdapter.updateItems(albumNames)
            }

            if (type == AddImageActivityType.Update) {
                val photoId = intent.getLongExtra("photo.id", 0)
                val photoDataList = viewModel.getPhotoById(photoId)

                runOnUiThread {
                    imageToSaveView.setImageBitmap(BitmapConverter.stringToBitmap(photoDataList[0]))
                    inputName.setText(photoDataList[1])
                    spinnerAlbumName.setSelection(spinnerAdapter.getPosition(photoDataList[2]))
                    inputDate.setText(photoDataList[3])
                }
            }
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
            if(type != AddImageActivityType.Create)
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

            // Show image
            imageToSaveView.setImageURI(imageUri)

            inputName.setText(viewModel.defaultPhotoName)
            inputDate.setText(viewModel.currentDateString)
        }

        buttonSave.setOnClickListener {
            val name = inputName.text.toString()
            val date = inputDate.text.toString()
            val imageString = BitmapConverter.bitmapToString(imageToSaveView.drawable.toBitmap())
            val isNewAlbum = newAlbumCheckbox.isChecked
            val album = if(isNewAlbum) inputAlbumName.text.toString() else spinnerAlbumName.selectedItem as String

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (type == AddImageActivityType.Create) { // Save
                        viewModel.savePhoto(name, date, imageString, album, isNewAlbum)
                    } else if (type == AddImageActivityType.Update) { // Update
                        val id = intent.getLongExtra("photo.id", 0)
                        viewModel.updatePhoto(id, name, date, imageString, album, isNewAlbum)
                    }

                    runOnUiThread {
                        if(type == AddImageActivityType.Create)
                            Toast.makeText(this@AddImageActivity, "Фотография создана", Toast.LENGTH_SHORT).show()
                        else if(type == AddImageActivityType.Update)
                            Toast.makeText(this@AddImageActivity, "Фотография изменена", Toast.LENGTH_SHORT).show()

                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
                catch(e: Exception){
                    if(e.message != null && e.message == "Wrong date format"){
                        runOnUiThread{
                            Toast.makeText(this@AddImageActivity, "Неверный формат даты", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable back arrow button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Enable menu
        if(type == AddImageActivityType.Create) {
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
                    val bitmap = BitmapConverter.stringToBitmap(viewModel.getRandomImageString())
                    runOnUiThread {
                        imageToSaveView.setImageBitmap(bitmap)
                        inputName.setText(viewModel.defaultPhotoName)
                        inputDate.setText(viewModel.currentDateString)
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

    enum class AddImageActivityType{
        Create, Update
    }
}