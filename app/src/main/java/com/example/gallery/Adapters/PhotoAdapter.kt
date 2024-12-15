package com.example.gallery.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.Model.Photo
import com.example.gallery.R

class PhotoAdapter(): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private lateinit var photos: ArrayList<Photo>
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var editButtonFunc: (photo: Photo, index: Int) -> Unit

    constructor(context: Context, photos: ArrayList<Photo>, editButtonFunc: (photo: Photo, index: Int) -> Unit): this(){
        this.photos = photos
        this.layoutInflater = LayoutInflater.from(context)
        this.editButtonFunc = editButtonFunc
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

        val album = photo.album
        val name = photo.name
        val date = photo.date

        viewHolder.photoImage.setImageBitmap(photo.bitmap)
        viewHolder.photoAlbumName.text = album + '/' + name
        viewHolder.photoDate.text = date
        viewHolder.editButton.setOnClickListener {
            editButtonFunc(photo, position)
        }
    }

    class PhotoViewHolder(var photoItem: View): RecyclerView.ViewHolder(photoItem) {
        val photoImage = photoItem.findViewById<ImageView>(R.id.photoImage)
        val photoAlbumName = photoItem.findViewById<TextView>(R.id.photoAlbumName)
        val photoDate = photoItem.findViewById<TextView>(R.id.photoDate)
        val editButton = photoItem.findViewById<ImageButton>(R.id.editPhotoButton)
    }
}