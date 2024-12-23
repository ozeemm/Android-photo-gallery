package com.example.gallery.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import com.example.gallery.Model.Album

class AlbumSpinnerAdapter(var ctx: Context, var resource: Int, var albums: ArrayList<Album>) : ArrayAdapter<Album>(ctx, resource, albums) {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(ctx)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val album = albums[position]

        val view = layoutInflater.inflate(resource, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = album.name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val album = albums[position]

        val view = layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        view.findViewById<CheckedTextView>(android.R.id.text1).text = album.name

        return view
    }
}