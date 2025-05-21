package com.example.gallery.Utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream

object BitmapConverter {

    fun bitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e:Exception) {
            e.printStackTrace()
            null
        }
    }

    fun inputStreamToBitmap(stream: InputStream): Bitmap{
        return BitmapFactory.decodeStream(stream)
    }

    fun inputStreamToString(stream: InputStream): String{
        return bitmapToString(inputStreamToBitmap(stream))
    }
}