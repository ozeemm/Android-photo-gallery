package com.example.gallery.Model

import android.graphics.Bitmap
import androidx.room.*
import com.example.gallery.Utils.BitmapConverter
import java.io.Serializable

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Photo(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name="image_string") var imageString: String,
) : Serializable{

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name="albumId")
    var albumId: Long = 0

    @Ignore
    var album: Album? = null

    val bitmap: Bitmap
        get() = BitmapConverter.stringToBitmap(imageString)!!
}