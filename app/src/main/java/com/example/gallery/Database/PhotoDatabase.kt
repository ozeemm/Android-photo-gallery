package com.example.gallery.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gallery.Model.*

@Database(
    entities = [Photo::class, Album::class],
    version = 3
)
abstract class PhotoDatabase: RoomDatabase() {

    companion object{
        val name = "gallery_database"
    }

    abstract fun photoDao(): IPhotoDao
    abstract fun albumDao(): IAlbumDao
}