package com.example.gallery.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @ColumnInfo(name = "name") var name: String
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}