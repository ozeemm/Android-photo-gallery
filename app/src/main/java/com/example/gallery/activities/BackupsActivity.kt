package com.example.gallery.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*

import com.example.gallery.adapters.BackupAdapter
import com.example.gallery.model.Backup // Не удалось удалить, так как используется в адаптере
import com.example.gallery.viewmodels.BackupsViewModel
import com.example.gallery.R
import com.example.gallery.databinding.ActivityBackupsBinding

class BackupsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBackupsBinding
    private lateinit var viewModel: BackupsViewModel
    private lateinit var backupAdapter: BackupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(BackupsViewModel::class.java)

        // Recycler View
        backupAdapter = BackupAdapter(this, ArrayList(emptyList<Backup>()), BackupButtonsListener())
        binding.recyclerViewBackups.adapter = backupAdapter

        viewModel.backups.observe(this) { backups ->
            backupAdapter.updateItems(backups)
        }

        // Create backup button
        binding.createBackupButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.createBackup()

                runOnUiThread {
                    Toast.makeText(this@BackupsActivity, getText(R.string.BackupsActivity_BackupCreated), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    inner class BackupButtonsListener : BackupAdapter.BackupButtonsListener{
        override fun onLoadClicked(backup: Backup) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.downloadBackup(backup)

                runOnUiThread {
                    Toast.makeText(this@BackupsActivity, getText(R.string.BackupsActivity_BackupDownloaded), Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDeleteClicked(backup: Backup) {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteBackup(backup)

                runOnUiThread {
                    Toast.makeText(this@BackupsActivity, getText(R.string.BackupsActivity_BackupDeleted), Toast.LENGTH_SHORT).show()
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