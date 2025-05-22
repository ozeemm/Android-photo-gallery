package com.example.gallery.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "albums")
data class Album(
    @ColumnInfo(name = "name") var name: String
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}