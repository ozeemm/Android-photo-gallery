package com.example.gallery

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.ArrayList

object StorageUtil {
    private val imagesDirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES + "/Android-photo-gallery-data").toString()

    public fun saveImage(name: String, bitmap: Bitmap): String?{
        val dirPath = File(imagesDirPath)
        val path = File(imagesDirPath, name)

        if(!dirPath.exists())
            dirPath.mkdir()

        try {
            val fs = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fs)
            fs.close()
        } catch(e: Exception) {
            e.printStackTrace()
            return null
        }

        return path.toString()
    }

    public fun getPhotos(): ArrayList<Photo>{
        val photos = ArrayList<Photo>()

        val dir = File(imagesDirPath)
        val listAllFiles = dir.listFiles()

        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                val photo = Photo()
                val uri = currentFile.absolutePath
                photo.uri = uri

                photos.add(photo)
            }
        }

        return photos
    }
}