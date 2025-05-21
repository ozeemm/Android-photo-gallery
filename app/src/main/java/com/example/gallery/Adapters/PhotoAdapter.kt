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
    private lateinit var editButtonFunc: (photo: Photo) -> Unit

    constructor(context: Context, photos: ArrayList<Photo>, editButtonFunc: (photo: Photo) -> Unit): this(){
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

        val album = photo.album!!.name
        val name = photo.name
        val date = photo.date

        viewHolder.photoImage.setImageBitmap(photo.bitmap)
        viewHolder.photoAlbumName.text = "$album/$name"
        viewHolder.photoDate.text = date
        viewHolder.editButton.setOnClickListener {
            editButtonFunc(photo)
        }
    }

    fun updateItems(photos: ArrayList<Photo>){
        this.photos.clear()
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }

    class PhotoViewHolder(private var photoItem: View): RecyclerView.ViewHolder(photoItem) {
        val photoImage: ImageView = photoItem.findViewById(R.id.photoImage)
        val photoAlbumName: TextView = photoItem.findViewById(R.id.photoAlbumName)
        val photoDate: TextView = photoItem.findViewById(R.id.photoDate)
        val editButton: ImageButton = photoItem.findViewById(R.id.editPhotoButton)
    }
}