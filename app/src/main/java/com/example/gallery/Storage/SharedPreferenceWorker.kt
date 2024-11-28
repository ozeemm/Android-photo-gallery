package com.example.gallery.Storage

import android.content.Context
import com.example.gallery.Model.Photo

class SharedPreferenceWorker(private var context: Context) : IImagesDataWorker {

    // ImagesData path: /data/data/com.example.gallery/shared_prefs/ImagesData.xml
    private val sharedPreferencesName = "ImagesData"
    private val imagesData = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
    private val separator = "|||"

    public override fun getPhotos(): ArrayList<Photo>{
        val photos = ArrayList<Photo>()

        val photosCount = imagesData.getInt("PhotosCount", -1)

        for(i in 0..<photosCount){
            val photoStr = imagesData.getString("Photo_$i", "")?.split(separator)!!
            val photo = Photo(photoStr[0], photoStr[1], photoStr[2], photoStr[3])
            photos.add(photo)
        }

        return photos
    }

    public override fun addPhoto(photo: Photo){
        val imagesDataEditor = imagesData.edit()

        var photosCount = imagesData.getInt("PhotosCount", -1)
        if(photosCount == -1) {
            imagesDataEditor.putInt("PhotosCount", 1)
            photosCount = 0
        }
        else
            imagesDataEditor.putInt("PhotosCount", photosCount + 1)

        val photoStr = photo.toString(separator)
        imagesDataEditor.putString("Photo_$photosCount", photoStr)

        imagesDataEditor.apply()
    }

    public override fun updatePhoto(index: Int, photo: Photo){
        val imagesDataEditor = imagesData.edit()

        val photoStr = photo.toString(separator)
        imagesDataEditor.putString("Photo_$index", photoStr)

        imagesDataEditor.apply()
    }

    public override fun deletePhoto(index: Int){
        val imagesDataEditor = imagesData.edit()

        val photosCount = imagesData.getInt("PhotosCount", -1)

        for(i in 0..<photosCount-1){
            if(i >= index){
                imagesDataEditor.putString("Photo_$i", imagesData.getString("Photo_${i+1}", ""))
            }
        }

        imagesDataEditor.remove("Photo_${photosCount-1}")
        imagesDataEditor.putInt("PhotosCount", photosCount-1)

        imagesDataEditor.apply()
    }
}