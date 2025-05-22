package com.example.gallery.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.model.Photo
import com.example.gallery.databinding.PhotoItemBinding

class PhotoAdapter(): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private lateinit var photos: ArrayList<Photo>
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var editButtonFunc: (photo: Photo) -> Unit

    constructor(context: Context, photos: ArrayList<Photo>, editButtonFunc: (photo: Photo) -> Unit): this(){
        this.photos = photos
        this.layoutInflater = LayoutInflater.from(context)
        this.editButtonFunc = editButtonFunc
    }

    inner class PhotoViewHolder(private val binding: PhotoItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo){
            binding.photo = photo
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(viewHolder: PhotoViewHolder, position: Int) {
        viewHolder.bind(photos[position])
    }

    fun updateItems(photos: ArrayList<Photo>){
        this.photos.clear()
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }
}