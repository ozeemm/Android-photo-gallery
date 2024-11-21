package com.example.gallery

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var photos: ArrayList<Photo>
    private lateinit var photoAdapter: PhotoAdapter

    private lateinit var gestureDetector: GestureDetector

    private val CREATE_PHOTO = 1
    private val UPDATE_PHOTO = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gestureDetector = GestureDetector(this, SwipeListener())

        photos = StorageUtil.getPhotos()

        // Fill recycle view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        photoAdapter = PhotoAdapter(this, photos, { photo: Photo, index: Int ->
            val intent = Intent(this, AddImageActivity::class.java)
            intent.putExtra("type", "update")
            intent.putExtra("photo", photo)
            intent.putExtra("index", index)
            startActivityForResult(intent, UPDATE_PHOTO)
        })
        recyclerView.adapter = photoAdapter

        // Delete photos on swipe down
        val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val index = viewHolder.adapterPosition
                    val photoToDelete = photos[index]

                    StorageUtil.deleteImage(photoToDelete)
                    photos.remove(photoToDelete)
                    photoAdapter.notifyDataSetChanged()

                    showText("Фотография удалена")
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showText(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
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
            startActivityForResult(intent, CREATE_PHOTO)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK)
            return

        when(requestCode){
            CREATE_PHOTO -> {
                val photo = data!!.getSerializableExtra("photo") as Photo
                photos.add(photo)
                photoAdapter.notifyDataSetChanged()
            }
            UPDATE_PHOTO -> {
                val photo = data!!.getSerializableExtra("photo") as Photo
                val index = data!!.getIntExtra("index", -1)

                photos[index].copyFrom(photo)
                photoAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    private fun onSwipeUp(){
        val intent = Intent(this, AddImageActivity::class.java)
        intent.putExtra("type", "create")
        startActivityForResult(intent, CREATE_PHOTO)
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