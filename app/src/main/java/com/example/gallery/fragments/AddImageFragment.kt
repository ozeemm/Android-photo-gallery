package com.example.gallery.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.gallery.R
import com.example.gallery.adapters.AlbumSpinnerAdapter
import com.example.gallery.databinding.FragmentAddImageBinding
import com.example.gallery.utils.BitmapConverter
import com.example.gallery.viewmodels.AddImageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class AddImageFragment : Fragment() {

    private lateinit var binding: FragmentAddImageBinding
    private lateinit var viewModel: AddImageViewModel

    private lateinit var type: AddImageFragmentType
    private lateinit var chooseImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddImageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddImageViewModel::class.java)

        // toolbar
        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(binding.addImageToolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.addMenuProvider(AddImageMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)

        val spinnerAdapter = AlbumSpinnerAdapter(context!!, ArrayList(emptyList()))
        binding.spinnerAlbumName.adapter = spinnerAdapter

        type = AddImageFragmentType.valueOf(arguments!!.getString("type")!!)

        CoroutineScope(Dispatchers.IO).launch {
            val albumNames = viewModel.getAlbumsNames()

            activity.runOnUiThread{
                spinnerAdapter.updateItems(albumNames)
            }

            if (type == AddImageFragmentType.Update) {
                val photoId = arguments!!.getLong("photo.id")
                val photoDataList = viewModel.getPhotoById(photoId)

                activity.runOnUiThread {
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
            if(type != AddImageFragmentType.Create)
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
                    if (type == AddImageFragmentType.Create) { // Save
                        viewModel.savePhoto(name, date, imageString, album, isNewAlbum)
                    } else if (type == AddImageFragmentType.Update) { // Update
                        val id = arguments!!.getLong("photo.id")
                        viewModel.updatePhoto(id, name, date, imageString, album, isNewAlbum)
                    }

                    activity.runOnUiThread {
                        if(type == AddImageFragmentType.Create)
                            makeText(getString(R.string.AddImageActivity_PhotoCreated))
                        else if(type == AddImageFragmentType.Update)
                            makeText(getString(R.string.AddImageActivity_PhotoUpdated))
                        finish()
                    }
                }
                catch(e: Exception){
                    if(e.message != null && e.message == "Wrong date format"){
                        activity.runOnUiThread{
                            makeText(getString(R.string.AddImageActivity_WrongDateFormat))
                        }
                    }
                }
            }
        }
    }

    inner class AddImageMenuProvider: MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            if(type == AddImageFragmentType.Create) {
                menuInflater.inflate(R.menu.add_image_menu, menu)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when(menuItem.itemId){
                android.R.id.home -> finish()
                R.id.menuRandomImage -> {
                    CoroutineScope(Dispatchers.IO).launch{
                        try {
                            val bitmap = BitmapConverter.stringToBitmap(viewModel.getRandomImageString())
                            activity?.runOnUiThread {
                                binding.imageToSaveView.setImageBitmap(bitmap)
                                binding.inputName.setText(viewModel.defaultPhotoName)
                                binding.inputDate.setText(viewModel.currentDateString)
                            }
                        }
                        catch(e: IOException){
                            makeText(getText(R.string.AddImageActivity_UnableToLoadRandomPhoto))
                        }
                    }
                }
                else -> return false
            }

            return true
        }
    }

    private fun makeText(text: CharSequence){
        activity?.runOnUiThread {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun finish(){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, MainFragment())
            .commit()
    }

    enum class AddImageFragmentType{
        Create, Update
    }
}