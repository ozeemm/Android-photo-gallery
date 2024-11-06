package com.example.gallery

import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object StorageUtil {
    private val imagesDirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES + "/Android-photo-gallery-data").toString()

    public fun saveImage(name: String, bitmap: Bitmap): String?{
        val path = File(imagesDirPath, name)

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

    public fun getImagesUri(): ArrayList<String>{
        val imagesUri = ArrayList<String>()

        val dir = File(imagesDirPath)
        val listAllFiles = dir.listFiles()

        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
            for (currentFile in listAllFiles) {
                imagesUri.add(currentFile.absolutePath)
            }
        }

        return imagesUri
    }
}