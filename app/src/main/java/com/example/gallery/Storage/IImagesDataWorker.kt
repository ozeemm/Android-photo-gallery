package com.example.gallery.Storage

import com.example.gallery.Model.Photo

interface IImagesDataWorker {
    public fun getPhotos(): ArrayList<Photo>
    public fun addPhoto(photo: Photo)
    public fun updatePhoto(index: Int, photo: Photo)
    public fun deletePhoto(index: Int)
}