package com.example.gallery.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.*
import com.example.gallery.Adapters.PhotoAdapter
import com.example.gallery.Model.Album
import com.example.gallery.Model.Photo
import com.example.gallery.Storage.*
import kotlin.math.abs
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private var photos = ArrayList<Photo>()
    private lateinit var photoAdapter: PhotoAdapter

    private lateinit var gestureDetector: GestureDetector

    private lateinit var createPhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var updatePhotoLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gestureDetector = GestureDetector(this, SwipeListener())

        createPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK)
                return@registerForActivityResult

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                val photo = result.data!!.getSerializableExtra("photo", Photo::class.java)!!
                val isNewAlbum = result.data!!.getBooleanExtra("is_new_album", false)

                CoroutineScope(Dispatchers.IO).launch{
                    if(isNewAlbum){
                        launch {
                            val albumName = result.data!!.getStringExtra("new_album")!!
                            val album = Album(albumName)

                            val albumId = App.database.albumDao().insertAlbum(album)
                            photo.albumId = albumId
                        }.join()
                    }

                    launch {
                        App.database.photoDao().insertPhoto(photo)
                    }
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
                    if(isNewAlbum) {
                        launch {
                            val albumName = result.data!!.getStringExtra("new_album")!!
                            val album = Album(albumName)

                            val albumId = App.database.albumDao().insertAlbum(album)
                            photo.albumId = albumId

                        }.join()
                    }

                    launch {
                        App.database.photoDao().updatePhoto(photo)
                    }.join()

                    launch {
                        val photosInAlbum = App.database.albumDao().getPhotosInAlbumCount(photo.album!!.id)
                        if(photosInAlbum == 0)
                            App.database.albumDao().deleteAlbum(photo.album!!)
                    }
                }
            }
        }

        // Fill recycle view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        photoAdapter = PhotoAdapter(this, photos, { photo: Photo, index: Int ->
            val intent = Intent(this, AddImageActivity::class.java)
            intent.putExtra("type", "update")
            intent.putExtra("photo", photo)
            updatePhotoLauncher.launch(intent)
        })
        recyclerView.adapter = photoAdapter

        App.database.photoDao().getPhotos().observe(this){ list ->
            CoroutineScope(Dispatchers.IO).launch {
                photos.clear()
                photos.addAll(list)
                photos.forEach { p ->
                    p.album = App.database.albumDao().getAlbumById(p.albumId)
                }

                runOnUiThread {
                    photoAdapter.notifyDataSetChanged()
                }
            }
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
                        val photoToDelete = photos[index]

                        launch {
                            App.database.photoDao().deletePhoto(photoToDelete)
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Фотография удалена", Toast.LENGTH_SHORT).show()
                            }
                        }.join()

                        launch {
                            val photosInAlbum = App.database.albumDao().getPhotosInAlbumCount(photoToDelete.album!!.id)
                            if(photosInAlbum == 0)
                                App.database.albumDao().deleteAlbum(photoToDelete.album!!)
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
            Thread {
                PicturesExporter.exportAll(photos)
                runOnUiThread {
                    Toast.makeText(this, "Экспортировано", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }
        else if(item.itemId == R.id.menuItemExportPdf){
            Thread {
                PdfExporter.export(photos)
                runOnUiThread {
                    Toast.makeText(this, "Экспортировано в PDF", Toast.LENGTH_SHORT).show()
                }
            }.start()
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