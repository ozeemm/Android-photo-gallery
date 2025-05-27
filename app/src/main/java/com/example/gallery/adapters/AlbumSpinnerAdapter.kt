package com.example.gallery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import com.example.gallery.databinding.SpinnerDropdownItemBinding
import com.example.gallery.databinding.SpinnerItemBinding

class AlbumSpinnerAdapter(
    private var ctx: Context,
    private var albums: ArrayList<String>
) : ArrayAdapter<String>(ctx, 0, albums) {

    // ViewHolder для обычного отображения
    private class NormalViewHolder(val textView: TextView)

    // ViewHolder для выпадающего списка
    private class DropdownViewHolder(val checkedTextView: CheckedTextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: NormalViewHolder

        if (convertView == null) {
            val binding = SpinnerItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            view = binding.root
            holder = NormalViewHolder(binding.text1)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as NormalViewHolder
        }

        holder.textView.text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: DropdownViewHolder

        if (convertView == null) {
            val binding = SpinnerDropdownItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            view = binding.root
            holder = DropdownViewHolder(binding.text1)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as DropdownViewHolder
        }

        holder.checkedTextView.text = getItem(position)
        return view
    }

    fun updateItems(albums: ArrayList<String>){
        this.clear()
        this.addAll(albums)
    }
}
