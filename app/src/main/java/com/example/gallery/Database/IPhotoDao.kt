package com.example.gallery.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gallery.Model.Photo

@Dao
interface IPhotoDao {

    @Query("SELECT * FROM photos")
    fun getPhotos(): LiveData<List<Photo>>

    @Insert
    suspend fun insertPhoto(photo: Photo)

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)
}