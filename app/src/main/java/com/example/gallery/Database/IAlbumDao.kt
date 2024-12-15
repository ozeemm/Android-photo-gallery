package com.example.gallery.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gallery.Model.Album

@Dao
interface IAlbumDao {
    @Query("SELECT * FROM albums")
    fun getAlbums(): LiveData<List<Album>>

    @Query("SELECT * FROM albums WHERE id = :id")
    fun getAlbumById(id: Long): Album

    @Insert
    fun insertAlbum(album: Album) : Long
}