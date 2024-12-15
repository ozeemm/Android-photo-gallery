package com.example.gallery.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "photos")
class Photo() : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name="name")
    var name: String? = null

    @ColumnInfo(name="date")
    var date: String? = null

    @ColumnInfo(name="album")
    var album: String? = null

    @ColumnInfo(name="uri")
    var uri: String? = null


    constructor(uri: String, name: String, date: String?, album: String?) : this() {
        this.uri = uri
        this.name = name
        this.date = date
        this.album = album
    }

    public fun copyFrom(photo: Photo){
        this.uri = photo.uri
        this.name = photo.name
        this.date = photo.date
        this.album = photo.album
    }

    public fun toString(separator: String): String {
        return uri + separator + name + separator + date + separator + album
    }
}