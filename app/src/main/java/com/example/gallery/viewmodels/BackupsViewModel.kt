package com.example.gallery.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gallery.App
import com.example.gallery.model.Album
import com.example.gallery.model.Backup
import com.example.gallery.model.Photo
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

class BackupsViewModel: ViewModel() {
    private val gson = Gson()
    private val apiUrl = "http://10.0.2.2:5232/api" // localhost

    private val _backups = MutableLiveData<ArrayList<Backup>>().apply {
        value = ArrayList()
    }
    val backups: LiveData<ArrayList<Backup>> = _backups

    private val photos = ArrayList<Photo>()
    private val albums = ArrayList<Album>()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            val albumsList = App.database.albumDao().fetchAlbums()
            albums.clear()
            albums.addAll(albumsList)

            val photosList = App.database.photoDao().fetchPhotos()
            photos.clear()
            photos.addAll(photosList)

            loadBackups()
        }
    }

    // Загрузить бэкапы для просмотра

    private fun loadBackups(){
        CoroutineScope(Dispatchers.IO).launch {
            // request
            val json = async{
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
            }.await()

            CoroutineScope(Dispatchers.Main).launch {
                _backups.value = ArrayList(gson.fromJson(json, Array<Backup>::class.java).toList())
            }
        }
    }

    // Создание нового бэкапа

    suspend fun createBackup(){
        val json = createBackupJson().await()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toRequestBody(mediaType)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$apiUrl/backups")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful)
            throw IOException(response.message)

        loadBackups()
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

    // Удаление бэкапа

    suspend fun deleteBackup(backup: Backup){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$apiUrl/backups/${backup.id}")
            .delete()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful)
            throw IOException(response.message)

        loadBackups()
    }

    // Скачать и использовать бэкап

    suspend fun downloadBackup(backup: Backup){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$apiUrl/backups/${backup.id}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful)
            return

        App.database.albumDao().deleteAllAlbums()

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
    }
}