package com.example.gallery.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.model.Backup
import com.example.gallery.databinding.BackupItemBinding
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

    inner class BackupViewHolder(private val binding: BackupItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(backup: Backup){
            binding.backup = backup
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        val binding = BackupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BackupViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: BackupViewHolder, position: Int) {
        viewHolder.bind(backups[position])
    }

    override fun getItemCount(): Int {
        return backups.size
    }

    fun updateItems(backups: ArrayList<Backup>){
        this.backups.clear()
        this.backups.addAll(backups)
        notifyDataSetChanged()
    }

    interface BackupButtonsListener{
        fun onLoadClicked(backup: Backup)
        fun onDeleteClicked(backup: Backup)
    }
}