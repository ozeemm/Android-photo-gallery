package com.example.gallery.Adapters

import android.content.Context
import android.icu.text.SimpleDateFormat
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
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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

    class BackupViewHolder(var backupItem: View) : RecyclerView.ViewHolder(backupItem){
        val dateText = backupItem.findViewById<TextView>(R.id.backupDateTextView)
        val albumsCountText = backupItem.findViewById<TextView>(R.id.albumsCountTextView)
        val photosCountText = backupItem.findViewById<TextView>(R.id.photosCountTextView)
        val loadButton = backupItem.findViewById<Button>(R.id.loadButton)
        val deleteButton = backupItem.findViewById<Button>(R.id.deleteButton)
    }

    interface BackupButtonsListener{
        fun onLoadClicked(backup: Backup)
        fun onDeleteClicked(backup: Backup)
    }
}