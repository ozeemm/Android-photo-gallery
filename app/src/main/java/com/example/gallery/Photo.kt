package com.example.gallery

import java.text.SimpleDateFormat
import java.util.*

class Photo() {
    companion object{
        private var nextId: Int = 0
            get() = field++
    }

    var id: Int = nextId
    lateinit var album: String
    lateinit var date: String

    constructor(_album: String): this(){
        album = if(_album == "") "Без альбома" else _album

        val sdf = SimpleDateFormat("dd.mm.yyyy hh:mm", Locale.getDefault())
        date = sdf.format(Date())
    }
}