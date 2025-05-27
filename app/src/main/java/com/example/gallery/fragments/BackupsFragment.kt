package com.example.gallery.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.gallery.R
import com.example.gallery.adapters.BackupAdapter
import com.example.gallery.databinding.FragmentBackupsBinding
import com.example.gallery.model.Backup
import com.example.gallery.viewmodels.BackupsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackupsFragment : Fragment() {
    private lateinit var binding: FragmentBackupsBinding
    private lateinit var viewModel: BackupsViewModel
    private lateinit var backupAdapter: BackupAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBackupsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(BackupsViewModel::class.java)

        // toolbar
        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(binding.backupsToolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.addMenuProvider(BackupsMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Recycler View
        backupAdapter = BackupAdapter(context!!, ArrayList(emptyList<Backup>()), BackupButtonsListener())
        binding.recyclerViewBackups.adapter = backupAdapter

        viewModel.backups.observe(this) { backups ->
            backupAdapter.updateItems(backups)
        }

        // Create backup button
        binding.createBackupButton.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.createBackup()
                makeText(getText(R.string.BackupsActivity_BackupCreated))
            }
        }
    }

    inner class BackupButtonsListener : BackupAdapter.BackupButtonsListener{
        override fun onLoadClicked(backup: Backup) {
            println("LOAD")
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.downloadBackup(backup)
                makeText(getText(R.string.BackupsActivity_BackupDownloaded))
            }
        }

        override fun onDeleteClicked(backup: Backup) {
            println("DELETE")
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteBackup(backup)
                makeText(getText(R.string.BackupsActivity_BackupDeleted))
            }
        }
    }

    inner class BackupsMenuProvider: MenuProvider{
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when(menuItem.itemId){
                android.R.id.home -> finish()
                else -> return false
            }

            return true
        }
    }

    private fun makeText(text: CharSequence){
        activity?.runOnUiThread {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun finish(){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, MainFragment())
            .commit()
    }
}