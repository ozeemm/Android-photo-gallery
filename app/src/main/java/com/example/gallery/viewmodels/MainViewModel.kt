package com.example.gallery.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.gallery.App
import com.example.gallery.storage.IStorageExporter
import com.example.gallery.model.Photo
import com.example.gallery.storage.PdfExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val storageExporter: IStorageExporter
): ViewModel() {

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

    suspend fun exportPhotos(): String{
        storageExporter.export(_photos.value!!)
        return storageExporter.onExportedMessage
    }

    suspend fun deletePhoto(index: Int){
        val photo = _photos.value!![index]

        App.database.photoDao().deletePhoto(photo)
        App.database.albumDao().deleteEmptyAlbums()
    }
}