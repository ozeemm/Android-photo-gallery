package com.example.gallery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

class MainActivity : AppCompatActivity() {

    private lateinit var photos: ArrayList<Photo>
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photos = StorageUtil.getPhotos()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        photoAdapter = PhotoAdapter(this, photos)
        recyclerView.adapter = photoAdapter

        val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.Callback(){
                override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                    return makeMovementFlags(0, ItemTouchHelper.DOWN)
                }

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
            startActivityForResult(intent, 1)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != RESULT_OK)
            return

        val photo = data!!.getSerializableExtra("Photo") as Photo
        photos.add(photo)
        photoAdapter.notifyDataSetChanged()
    }
}