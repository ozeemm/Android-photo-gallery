package com.example.gallery.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gallery.App
import com.example.gallery.Model.Album
import com.example.gallery.Model.Photo
import com.example.gallery.Storage.PdfExporter
import com.example.gallery.Storage.PicturesExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _photos = MutableLiveData<ArrayList<Photo>>().apply {
        value = ArrayList()
    }

    val photos: LiveData<ArrayList<Photo>> = _photos

    private val photosObserver = Observer<List<Photo>> { list ->
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach{ p ->
                p.album = App.database.albumDao().getAlbumById(p.albumId)
            }

            CoroutineScope(Dispatchers.Main).launch {
                _photos.value = ArrayList(list)
            }
        }
    }

    init{
        App.database.photoDao().getPhotos().observeForever(photosObserver)
    }

    override fun onCleared() {
        App.database.photoDao().getPhotos().removeObserver(photosObserver)
    }

    public suspend fun exportPhotosStorage(){
        PicturesExporter.exportAll(_photos.value!!)
    }

    public suspend fun exportPhotosPdf(){
        PdfExporter.export(_photos.value!!)
    }

    public suspend fun createPhoto(photo: Photo){
        App.database.photoDao().insertPhoto(photo)
    }

    public suspend fun createPhotoInNewAlbum(photo: Photo, albumName: String){
        val album = Album(albumName)
        val albumId = App.database.albumDao().insertAlbum(album)
        photo.albumId = albumId
        createPhoto(photo)
    }

    public suspend fun updatePhoto(photo: Photo){
        App.database.photoDao().updatePhoto(photo)
        deleteAlbumIfEmptyAfterChangePhoto(photo)
    }

    public suspend fun updatePhotoNewAlbum(photo: Photo, albumName: String){
        val album = Album(albumName)
        val albumId = App.database.albumDao().insertAlbum(album)
        photo.albumId = albumId
        updatePhoto(photo)
    }

    private suspend fun deleteAlbumIfEmptyAfterChangePhoto(photo: Photo){
        val photosInAlbum = App.database.albumDao().getPhotosInAlbumCount(photo.album!!.id)
        if(photosInAlbum == 0)
            App.database.albumDao().deleteAlbum(photo.album!!)
    }

    public suspend fun deletePhoto(index: Int){
        val photo = _photos.value!![index]
        App.database.photoDao().deletePhoto(photo)
        deleteAlbumIfEmptyAfterChangePhoto(photo)
    }
}