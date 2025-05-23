package com.example.gallery.di

import com.example.gallery.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.gallery.storage.IStorageExporter
import com.example.gallery.storage.PicturesExporter

object AppModules {
    val storageExporterModule = module{
        single<IStorageExporter>{ PicturesExporter() }
        viewModelOf(::MainViewModel)
    }
}