package com.example.gallery.adapters

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:bitmap")
fun setImageBitmap(imageView: ImageView, bitmap: Bitmap?){
    if(bitmap != null)
        imageView.setImageBitmap(bitmap)
    else
        imageView.setImageDrawable(null)
}

@BindingAdapter("android:datetimeText")
fun setDatetimeText(textView: TextView, text: String?){
    if(text != null)
        textView.text = text.replace('T', ' ')
    else
        textView.text = ""
}