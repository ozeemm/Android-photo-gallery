package com.example.gallery.Database

import androidx.room.*
import com.example.gallery.Model.Photo

@Dao
interface IPhotoDao {

    @Query("SELECT * FROM photos")
    fun getPhotos(): List<Photo>

    @Insert
    fun insertPhoto(photo: Photo)

    @Update
    fun updatePhoto(photo: Photo)

    @Delete
    fun deletePhoto(photo: Photo)
}