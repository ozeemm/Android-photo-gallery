package com.example.gallery

import java.io.Serializable

class Photo() : Serializable {
    var uri: String? = null
    var name: String? = null
    var date: String? = null
    var album: String? = null

    constructor(uri: String, name: String, date: String?, album: String?) : this() {
        this.uri = uri
        this.name = name
        this.date = date
        this.album = album
    }
}