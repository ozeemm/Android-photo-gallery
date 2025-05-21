package com.example.gallery.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gallery.Model.Album

@Dao
interface IAlbumDao {
    @Query("SELECT * FROM albums")
    suspend fun fetchAlbums(): List<Album>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): Album

    @Query("SELECT * FROM albums WHERE name == :name")
    suspend fun getAlbumByName(name: String): Album

    @Insert
    suspend fun insertAlbum(album: Album) : Long

    @Query("DELETE FROM albums")
    suspend fun deleteAllAlbums()

    @Query("DELETE FROM albums " +
            "WHERE id NOT IN (SELECT DISTINCT albumId FROM photos);")
    suspend fun deleteEmptyAlbums()
}