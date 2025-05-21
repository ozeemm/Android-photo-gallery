package com.example.gallery.Adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.Model.Backup
import com.example.gallery.R
import kotlin.collections.ArrayList

class BackupAdapter() : RecyclerView.Adapter<BackupAdapter.BackupViewHolder>() {

    private lateinit var layoutInflater: LayoutInflater
    private lateinit var backups: ArrayList<Backup>
    private lateinit var backupButtonsListener: BackupButtonsListener

    constructor(context: Context,
                backups: ArrayList<Backup>,
                backupButtonsListener: BackupButtonsListener
        ) : this(){
        this.layoutInflater = LayoutInflater.from(context)
        this.backups = backups
        this.backupButtonsListener = backupButtonsListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        val view = layoutInflater.inflate(R.layout.backup_item, parent, false)
        return BackupViewHolder(view)
    }

    override fun getItemCount(): Int {
        return backups.size
    }

    fun updateItems(backups: ArrayList<Backup>){
        this.backups.clear()
        this.backups.addAll(backups)
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: BackupViewHolder, position: Int) {
        val backup = backups[position]

        viewHolder.dateText.text = backup.date.replace('T', ' ')
        viewHolder.albumsCountText.text = backup.albumsCount.toString()
        viewHolder.photosCountText.text = backup.photosCount.toString()

        viewHolder.loadButton.setOnClickListener{
            backupButtonsListener.onLoadClicked(backup)
        }
        viewHolder.deleteButton.setOnClickListener{
            backupButtonsListener.onDeleteClicked(backup)
        }
    }

    class BackupViewHolder(private var backupItem: View) : RecyclerView.ViewHolder(backupItem){
        val dateText: TextView = backupItem.findViewById(R.id.backupDateTextView)
        val albumsCountText: TextView = backupItem.findViewById(R.id.albumsCountTextView)
        val photosCountText: TextView = backupItem.findViewById(R.id.photosCountTextView)
        val loadButton: Button = backupItem.findViewById(R.id.loadButton)
        val deleteButton: Button = backupItem.findViewById(R.id.deleteButton)
    }

    interface BackupButtonsListener{
        fun onLoadClicked(backup: Backup)
        fun onDeleteClicked(backup: Backup)
    }
}