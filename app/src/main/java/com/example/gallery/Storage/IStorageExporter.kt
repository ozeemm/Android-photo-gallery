package com.example.gallery.Storage

import com.example.gallery.model.Photo

interface IStorageExporter {
    val onExportedMessage: String
        get() = "Экспортировано"

    suspend fun export(photos: ArrayList<Photo>)
}