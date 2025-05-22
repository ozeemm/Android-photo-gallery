package com.example.gallery.Activities

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlinx.coroutines.*

import com.example.gallery.R
import com.example.gallery.Adapters.PhotoAdapter
import com.example.gallery.ViewModels.MainViewModel
import com.example.gallery.Model.Photo // Не удалось удалить, так как используется в Extras
import com.example.gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        gestureDetector = GestureDetector(this, SwipeListener())

        // Fill recycle view
        photoAdapter = PhotoAdapter(this, ArrayList(emptyList())) { photo: Photo ->
            val intent = Intent(this, AddImageActivity::class.java)
            intent.putExtra("type", "Update")
            intent.putExtra("photo.id", photo.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = photoAdapter

        viewModel.photos.observe(this) { list ->
            photoAdapter.updateItems(list)
        }

        // Delete photos on swipe down
        val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val index = viewHolder.adapterPosition
                        viewModel.deletePhoto(index)

                        runOnUiThread {
                            Toast.makeText(this@MainActivity, getText(R.string.MainActivity_PhotoDeleted), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable Menu
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Open activity from menu
        when (item.itemId) {
            R.id.menuItemAdd -> {
                val intent = Intent(this, AddImageActivity::class.java)
                intent.putExtra("type", "Create")
                startActivity(intent)
            }
            R.id.menuItemExportPictures -> {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.exportPhotosStorage()

                    runOnUiThread {
                        Toast.makeText(this@MainActivity, R.string.MainActivity_ExportedInStorage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.menuItemExportPdf -> {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.exportPhotosPdf()

                    runOnUiThread {
                        Toast.makeText(this@MainActivity, R.string.MainActivity_ExportedInPdf, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.menuItemBackup -> {
                val intent = Intent(this, BackupsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    private fun onSwipeUp(){
        val intent = Intent(this, AddImageActivity::class.java)
        intent.putExtra("type", "Create")
        startActivity(intent)
    }

    inner class SwipeListener: GestureDetector.SimpleOnGestureListener(){

        private val SWIPE_THRESHOLD: Int = 50
        private val SWIPE_VELOCITY_THRESHOLD: Int = 50

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1.x

            if(abs(diffY) > abs(diffX)){
                if(diffY < 0 && abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)
                    onSwipeUp()
            }

            return true
        }
    }
}