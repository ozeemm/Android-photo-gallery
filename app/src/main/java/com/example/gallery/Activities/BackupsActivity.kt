package com.example.gallery.Activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import com.example.gallery.Adapters.BackupAdapter
import com.example.gallery.Model.*
import com.example.gallery.ViewModels.BackupsViewModel
import com.example.gallery.R

class BackupsActivity : AppCompatActivity() {
    private lateinit var viewModel: BackupsViewModel
    private lateinit var backupAdapter: BackupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backups)
        viewModel = ViewModelProvider(this).get(BackupsViewModel::class.java)

        // Recycler View
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBackups)
        backupAdapter = BackupAdapter(this, ArrayList(emptyList<Backup>()), BackupButtonsListener())
        recyclerView.adapter = backupAdapter

        viewModel.backups.observe(this, { backups ->
            backupAdapter.updateItems(backups)
        })

        // Create backup button
        val createBackupButton = findViewById<Button>(R.id.createBackupButton)
        createBackupButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.createBackup()

                runOnUiThread {
                    Toast.makeText(this@BackupsActivity, "Резервная копия создана", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    inner class BackupButtonsListener : BackupAdapter.BackupButtonsListener{
        override fun onLoadClicked(backup: Backup) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.downloadBackup(backup)

                runOnUiThread {
                    Toast.makeText(this@BackupsActivity, "Резервная копия загружена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDeleteClicked(backup: Backup) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteBackup(backup)

                runOnUiThread {
                    Toast.makeText(this@BackupsActivity, "Резервная копия удалена", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Enable back arrow button
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Close on back arrow click
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}