package com.example.gallery.Storage

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Environment
import com.example.gallery.model.Photo
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

object PicturesExporter {
    private val downloadsDirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS + "/Android-photo-gallery-export").toString()

    suspend fun exportAll(photos: ArrayList<Photo>){
        val appDirPath = File(downloadsDirPath)
        if(!appDirPath.exists())
            appDirPath.mkdir()

        val dirPath = File(downloadsDirPath + "/${getCurrentDateString()}")
        if(!dirPath.exists())
            dirPath.mkdir()

        for (photo in photos) {
            val albumName = photo.album!!.name
            val albumDir = File(dirPath.path, albumName)
            if(!albumDir.exists())
                albumDir.mkdir()

            val path = File(albumDir.path, photo.name + ".png")
            exportPhoto(photo, path)
        }
    }

    private fun exportPhoto(photo: Photo, path: File){
        if(!path.exists())
            path.createNewFile()
        try{
            val fs = FileOutputStream(path)
            photo.bitmap.compress(Bitmap.CompressFormat.PNG, 100, fs)
            fs.close()
        } catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun getCurrentDateString(): String{
        return SimpleDateFormat("yyyy.MM.dd-HH.mm.ss", Locale.ENGLISH).format(Date())
    }
}