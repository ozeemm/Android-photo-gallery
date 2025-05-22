package com.example.gallery.storage

import com.example.gallery.model.Photo

interface IStorageExporter {
    val onExportedMessage: String
        get() = "Экспортировано"

    suspend fun export(photos: ArrayList<Photo>)
}