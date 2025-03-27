package com.example.gallery.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gallery.Model.Album

@Dao
interface IAlbumDao {
    @Query("SELECT * FROM albums")
    fun getAlbums(): LiveData<List<Album>>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): Album

    @Query("SELECT count(*) FROM photos WHERE photos.albumId == :albumId")
    suspend fun getPhotosInAlbumCount(albumId: Long): Int

    @Insert
    suspend fun insertAlbum(album: Album) : Long

    @Delete
    suspend fun deleteAlbum(album: Album)
}