package com.example.gallery.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import com.example.gallery.databinding.ActivityAddImageBinding

class AddImageActivity : AppCompatActivity()  {

    lateinit var binding: ActivityAddImageBinding
    private lateinit var viewModel: AddImageViewModel

    private lateinit var type: AddImageActivityType
    private lateinit var chooseImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(AddImageViewModel::class.java)

        val spinnerAdapter = AlbumSpinnerAdapter(this, ArrayList(emptyList()))
        binding.spinnerAlbumName.adapter = spinnerAdapter

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
                    binding.imageToSaveView.setImageBitmap(BitmapConverter.stringToBitmap(photoDataList[0]))
                    binding.inputName.setText(photoDataList[1])
                    binding.spinnerAlbumName.setSelection(spinnerAdapter.getPosition(photoDataList[2]))
                    binding.inputDate.setText(photoDataList[3])
                }
            }
        }

        binding.newAlbumNameCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.inputAlbumName.visibility = View.VISIBLE
                binding.spinnerAlbumName.visibility = View.INVISIBLE
            }
            else{
                binding.inputAlbumName.visibility = View.INVISIBLE
                binding.spinnerAlbumName.visibility = View.VISIBLE
            }
        }

        binding.imageToSaveView.setOnClickListener {
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
            binding.imageToSaveView.setImageURI(imageUri)

            binding.inputName.setText(viewModel.defaultPhotoName)
            binding.inputDate.setText(viewModel.currentDateString)
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.inputName.text.toString()
            val date = binding.inputDate.text.toString()
            val bitmap = binding.imageToSaveView.drawable.toBitmap()
            val imageString = BitmapConverter.bitmapToString(bitmap)
            val isNewAlbum = binding.newAlbumNameCheckBox.isChecked
            val album = if(isNewAlbum)
                binding.inputAlbumName.text.toString()
            else
                binding.spinnerAlbumName.selectedItem as String

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
                            Toast.makeText(this@AddImageActivity, getString(R.string.AddImageActivity_PhotoCreated), Toast.LENGTH_SHORT).show()
                        else if(type == AddImageActivityType.Update)
                            Toast.makeText(this@AddImageActivity, getString(R.string.AddImageActivity_PhotoUpdated), Toast.LENGTH_SHORT).show()

                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
                catch(e: Exception){
                    if(e.message != null && e.message == "Wrong date format"){
                        runOnUiThread{
                            Toast.makeText(this@AddImageActivity, getString(R.string.AddImageActivity_WrongDateFormat), Toast.LENGTH_SHORT).show()
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
                        binding.imageToSaveView.setImageBitmap(bitmap)
                        binding.inputName.setText(viewModel.defaultPhotoName)
                        binding.inputDate.setText(viewModel.currentDateString)
                    }
                }
                catch(e: IOException){
                    runOnUiThread {
                        Toast.makeText(this@AddImageActivity, getText(R.string.AddImageActivity_UnableToLoadRandomPhoto), Toast.LENGTH_SHORT).show()
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