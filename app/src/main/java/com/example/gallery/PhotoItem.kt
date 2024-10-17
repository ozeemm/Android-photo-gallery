package com.example.gallery

class PhotoItem() {
    lateinit var album: String
    lateinit var date: String

    constructor(_album: String, _date: String): this(){
        album = if(_album == "") "Без альбома" else _album
        date = if(_date == "") "Без даты" else _date
    }
}