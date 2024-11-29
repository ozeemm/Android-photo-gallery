package com.example.gallery.Storage

import android.content.Context
import com.example.gallery.Model.Photo
import java.io.File
import java.lang.Integer.parseInt

class TextFileWorker(private var context: Context) : IImagesDataWorker {

    // ImagesData path: /data/data/com.example.gallery/files/ImagesData.txt
    private val fileName = "ImagesData.txt"
    private val file = File(context.filesDir, fileName)
    private val separator = "|||"

    override fun getPhotos(): ArrayList<Photo> {
        val photos = ArrayList<Photo>()

        if(!file.exists()){
            file.createNewFile()
            file.writeText("0")
        }

        file.readLines().withIndex().forEach(){ (i, line) ->
            if(i != 0){
                val photoStr = line.split(separator)
                val photo = Photo(photoStr[0], photoStr[1], photoStr[2], photoStr[3])
                photos.add(photo)
            }

        }

        return photos
    }

    override fun addPhoto(photo: Photo) {
        val lines = file.readLines() as ArrayList<String>
        lines[0] = (parseInt(lines[0]) + 1).toString()

        val writer = file.bufferedWriter()
        for(line in lines)
            writer.write(line + "\n")
        writer.write(photo.toString(separator) + "\n")
        writer.close()
    }

    override fun updatePhoto(index: Int, photo: Photo) {
        val lines = file.readLines() as ArrayList<String>

        val writer = file.bufferedWriter()
        for((i, line) in lines.withIndex()) {
            if(i == index+1)
                writer.write(photo.toString(separator) + "\n")
            else
                writer.write(line + "\n")
        }
        writer.close()
    }

    override fun deletePhoto(index: Int) {
        val lines = file.readLines() as ArrayList<String>
        lines[0] = (parseInt(lines[0]) - 1).toString()

        val writer = file.bufferedWriter()
        for((i, line) in lines.withIndex()) {
            if(i == index+1) continue
            writer.write(line + "\n")
        }
        writer.close()
    }
}