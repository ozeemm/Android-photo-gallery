package com.example.gallery

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(): RecyclerView.Adapter<PhotoViewHolder>() {
    private lateinit var photos: ArrayList<Photo>
    private lateinit var layoutInflater: LayoutInflater

    constructor(context: Context, photos: ArrayList<Photo>): this(){
        this.photos = photos
        this.layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = layoutInflater.inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(viewHolder: PhotoViewHolder, position: Int) {
        val photo = photos[position]

        val album = if (photo.album != null) photo.album else "Без альбома"
        val name = if(photo.name != null) photo.name else "Без названия"
        val date = if(photo.date != null) photo.date else "Без даты"

        viewHolder.photoImage.setImageURI(Uri.parse(photo.uri))
        viewHolder.photoAlbumName.text = album + '/' + name
        viewHolder.photoDate.text = date
    }

}