package com.example.gallery.Activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.Adapters.BackupAdapter
import com.example.gallery.App
import com.example.gallery.Model.*
import com.example.gallery.R
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class BackupsActivity : AppCompatActivity() {

    private lateinit var backupAdapter: BackupAdapter

    private val gson = Gson()
    private val apiUrl = "http://10.0.2.2:5232/api" // localhost
    private var backups: ArrayList<Backup> = ArrayList<Backup>()

    private val photos = ArrayList<Photo>()
    private val albums = ArrayList<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backups)

        App.database.albumDao().getAlbums().observe(this){list ->
            albums.clear()
            albums.addAll(list)
        }
        App.database.photoDao().getPhotos().observe(this){list ->
            photos.clear()
            photos.addAll(list)
        }

        // Recycler View
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBackups)
        backupAdapter = BackupAdapter(this, backups, BackupButtonsListener())
        recyclerView.adapter = backupAdapter

        // Create backup button
        val createBackupButton = findViewById<Button>(R.id.createBackupButton)
        createBackupButton.setOnClickListener{
            createBackup()
        }

        // Load backups
        loadBackups()
    }

    // Requests

    // Load
    private fun loadBackups(){
        CoroutineScope(Dispatchers.IO).launch {
            val json = loadBackupsJson().await()
            val list = ArrayList<Backup>(gson.fromJson(json, Array<Backup>::class.java).toList())

            backups.clear()
            backups.addAll(list)

            runOnUiThread{
                backupAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun loadBackupsJson(): Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("$apiUrl/backups")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return@async response.body?.string()!!
            } else {
                throw IOException(response.message)
            }
        }
    }

    private fun downloadBackup(backup: Backup){
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("$apiUrl/backups/${backup.id}")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if(!response.isSuccessful)
                return@launch

            deleteAlbums()

            val json = JsonParser.parseString(response.body?.string()!!).asJsonObject
            val albumsJson = json["albums"].asJsonArray

            for(albumJson in albumsJson){
                val albumName = albumJson.asJsonObject["name"].asString

                val album = Album(albumName)
                val albumId = App.database.albumDao().insertAlbum(album)

                val photosJson = albumJson.asJsonObject["photos"].asJsonArray
                for(p in photosJson){
                    val photoJson = p.asJsonObject
                    val photo = Photo(
                        photoJson["name"].asString,
                        photoJson["date"].asString,
                        photoJson["imageString"].asString
                    )
                    photo.albumId = albumId

                    App.database.photoDao().insertPhoto(photo)
                }
            }

            runOnUiThread{
                Toast.makeText(this@BackupsActivity, "Резервная копия загружена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Create
    private fun createBackup(){
        CoroutineScope(Dispatchers.IO).launch {
            val json = createBackupJson().await()

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull();
            val body = json.toRequestBody(mediaType)

            val client = OkHttpClient()
            val request = Request.Builder()
                .url("$apiUrl/backups")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if(!response.isSuccessful)
                throw IOException(response.message)

            runOnUiThread{
                Toast.makeText(this@BackupsActivity, "Резервная копия создана", Toast.LENGTH_SHORT).show()
            }

            loadBackups()
        }
    }

    private fun createBackupJson(): Deferred<String>{
        return CoroutineScope(Dispatchers.IO).async {
            val json = JsonArray()

            for(album in albums) {
                val albumElement = JsonObject()

                val photosArray = JsonArray()
                for (photo in photos) {
                    if (photo.albumId == album.id) {
                        val photoJson = JsonObject()
                        photoJson.addProperty("name", photo.name)
                        photoJson.addProperty("date", photo.date)
                        photoJson.addProperty("imageString", photo.imageString)
                        photosArray.add(photoJson)
                    }
                }

                albumElement.addProperty("name", album.name)
                albumElement.add("photos", photosArray)

                json.add(albumElement)
            }

            return@async gson.toJson(json)
        }
    }

    // Delete
    private fun deleteBackup(backup: Backup){
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("$apiUrl/backups/${backup.id}")
                .delete()
                .build()

            val response = client.newCall(request).execute()
            if(!response.isSuccessful)
                throw IOException(response.message)

            runOnUiThread{
                Toast.makeText(this@BackupsActivity, "Резервная копия удалена", Toast.LENGTH_SHORT).show()
            }

            loadBackups()
        }
    }

    private fun deleteAlbums(){
        CoroutineScope(Dispatchers.IO).launch {
            App.database.albumDao().deleteAllAlbums()
        }
    }

    inner class BackupButtonsListener : BackupAdapter.BackupButtonsListener{
        override fun onLoadClicked(backup: Backup) {
            downloadBackup(backup)
        }

        override fun onDeleteClicked(backup: Backup) {
            deleteBackup(backup)
        }

    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable back arrow button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Close on back arrow click
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}