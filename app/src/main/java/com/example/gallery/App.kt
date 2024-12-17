package com.example.gallery

import android.app.Application
import androidx.room.Room
import com.example.gallery.Database.PhotoDatabase

class App : Application() {
    companion object{
        lateinit var database: PhotoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            PhotoDatabase::class.java,
            PhotoDatabase.name
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}