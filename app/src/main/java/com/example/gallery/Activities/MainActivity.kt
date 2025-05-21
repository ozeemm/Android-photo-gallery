package com.example.gallery.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlinx.coroutines.*
import com.example.gallery.*
import com.example.gallery.Adapters.PhotoAdapter
import com.example.gallery.ViewModels.MainViewModel
import com.example.gallery.Model.Photo // Не удалось удалить, так как используется в Extras

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private lateinit var photoAdapter: PhotoAdapter

    private lateinit var gestureDetector: GestureDetector

    private lateinit var createPhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var updatePhotoLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        gestureDetector = GestureDetector(this, SwipeListener())

        createPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK)
                return@registerForActivityResult

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                val photo = result.data!!.getSerializableExtra("photo", Photo::class.java)!!
                val isNewAlbum = result.data!!.getBooleanExtra("is_new_album", false)

                CoroutineScope(Dispatchers.IO).launch{
                    if(isNewAlbum) {
                        val albumName = result.data!!.getStringExtra("new_album")!!
                        viewModel.createPhotoInNewAlbum(photo, albumName)
                    }
                    else
                        viewModel.createPhoto(photo)
                }
            }
        }

        updatePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK)
                return@registerForActivityResult

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val photo = result.data!!.getSerializableExtra("photo", Photo::class.java)!!
                val isNewAlbum = result.data!!.getBooleanExtra("is_new_album", false)

                CoroutineScope(Dispatchers.IO).launch {
                    if(isNewAlbum){
                        val albumName = result.data!!.getStringExtra("new_album")!!
                        viewModel.updatePhotoNewAlbum(photo, albumName)
                    }
                    else
                        viewModel.updatePhoto(photo)
                }
            }
        }

        // Fill recycle view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        photoAdapter = PhotoAdapter(this, ArrayList(emptyList()), { photo: Photo, index: Int ->
            val intent = Intent(this, AddImageActivity::class.java)
            intent.putExtra("type", "update")
            intent.putExtra("photo", photo)
            updatePhotoLauncher.launch(intent)
        })
        recyclerView.adapter = photoAdapter

        viewModel.photos.observe(this, { list ->
            photoAdapter.updateItems(list)
        })

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
                            Toast.makeText(this@MainActivity, "Фотография удалена", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(recyclerView)
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
            intent.putExtra("type", "create")
            createPhotoLauncher.launch(intent)
        }
        else if(item.itemId == R.id.menuItemExportPictures){
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.exportPhotosStorage()

                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Экспортировано", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if(item.itemId == R.id.menuItemExportPdf){
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.exportPhotosPdf()

                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Экспортировано в PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if(item.itemId == R.id.menuItemBackup){
            val intent = Intent(this, BackupsActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    private fun onSwipeUp(){
        val intent = Intent(this, AddImageActivity::class.java)
        intent.putExtra("type", "create")
        createPhotoLauncher.launch(intent)
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