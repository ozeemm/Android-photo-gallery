package com.example.gallery.viewmodels

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import com.example.gallery.App
import com.example.gallery.model.Album
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import com.example.gallery.model.Photo
import com.example.gallery.utils.BitmapConverter

class AddImageViewModel : ViewModel() {
    private val randomImagesUrl = "https://picsum.photos/800/600"

    val defaultPhotoName: String
        get() = "Image_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}"

    val currentDateString: String
        get() = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH).format(Date())

    suspend fun getAlbumsNames(): ArrayList<String>{
        return ArrayList(App.database.albumDao().fetchAlbums().map{ a -> a.name })
    }

    suspend fun getPhotoById(id: Long): List<String>{
        val photo = App.database.photoDao().getPhoto(id)
        val album = App.database.albumDao().getAlbumById(photo.albumId)
        return listOf( photo.imageString, photo.name, album.name, photo.date)
    }

    suspend fun savePhoto(name: String, date: String, image: String, album: String, isNewAlbum: Boolean){
        if(!isDateStringCorrect(date)){
            throw Exception("Wrong date format")
        }

        val photo = Photo(name, date, image)
        photo.albumId = getAlbumId(album, isNewAlbum)

        App.database.photoDao().insertPhoto(photo)
    }

    suspend fun updatePhoto(id: Long, name: String, date: String, image: String, album: String, isNewAlbum: Boolean){
        if(!isDateStringCorrect(date)){
            throw Exception("Wrong date format")
        }

        val photo = Photo(name, date, image)
        photo.id = id
        photo.albumId = getAlbumId(album, isNewAlbum)

        App.database.photoDao().updatePhoto(photo)
        App.database.albumDao().deleteEmptyAlbums()
    }

    suspend fun getRandomImageString(): String{
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(randomImagesUrl)
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return BitmapConverter.inputStreamToString(response.body?.byteStream()!!)
            } else {
                throw IOException()
            }
        } catch (e: IOException) {
            throw e
        }

    }

    private suspend fun getAlbumId(album: String, isNewAlbum: Boolean): Long{
        return if(isNewAlbum)
            App.database.albumDao().insertAlbum(Album(album))
        else
            App.database.albumDao().getAlbumByName(album).id
    }

    private fun isDateStringCorrect(dateString: String): Boolean{
        val pattern = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH)
        try {
            pattern.parse(dateString)
            return true
        } catch(e: Exception){
            return false
        }
    }
}