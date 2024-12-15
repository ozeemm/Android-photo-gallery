package com.example.gallery.Model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gallery.Utils.BitmapConverter
import java.io.Serializable

@Entity(tableName = "photos")
data class Photo(
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="date") var date: String,
    @ColumnInfo(name="album") var album: String,
    @ColumnInfo(name="image_string") var imageString: String,
) : Serializable{

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    val bitmap: Bitmap
        get() = BitmapConverter.stringToBitmap(imageString)!!
}