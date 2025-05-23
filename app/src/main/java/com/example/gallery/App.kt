package com.example.gallery

import android.app.Application
import androidx.room.Room
import com.example.gallery.Database.PhotoDatabase
import com.example.gallery.di.AppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    companion object{
        lateinit var database: PhotoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@App)
            modules(AppModules.storageExporterModule)
        }

        database = Room.databaseBuilder(
            applicationContext,
            PhotoDatabase::class.java,
            PhotoDatabase.name
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}