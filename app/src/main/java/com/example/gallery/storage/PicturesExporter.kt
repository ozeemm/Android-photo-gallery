package com.example.gallery.storage

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Environment
import com.example.gallery.model.Photo
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class PicturesExporter : IStorageExporter {
    private val downloadsDirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS + "/Android-photo-gallery-export").toString()

    override val onExportedMessage: String
        get() = "Экспортировано в хранилище (Downloads)"

    override suspend fun export(photos: ArrayList<Photo>){
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