package com.example.gallery

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotoViewHolder(var photoItem: View): RecyclerView.ViewHolder(photoItem) {
    val photoImage = photoItem.findViewById<ImageView>(R.id.photoImage)
    val photoAlbumName = photoItem.findViewById<TextView>(R.id.photoAlbumName)
    val photoDate = photoItem.findViewById<TextView>(R.id.photoDate)
}