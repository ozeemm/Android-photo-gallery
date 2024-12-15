package com.example.gallery.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gallery.Model.Photo

@Database(
    entities = [Photo::class],
    version = 1
)
abstract class PhotoDatabase: RoomDatabase() {

    companion object{
        val name = "gallery_database"
    }

    abstract fun photoDao(): IPhotoDao
}