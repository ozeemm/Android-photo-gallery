package org.example

import org.greenrobot.greendao.generator.DaoGenerator
import org.greenrobot.greendao.generator.Property
import org.greenrobot.greendao.generator.Schema

fun main() {
    val schema = Schema(1, "com.example.gallery.Database")
    schema.enableKeepSectionsByDefault()

    val photoEntity = schema.addEntity("photos")
    photoEntity.addIdProperty().primaryKey().autoincrement()
    photoEntity.addStringProperty("name")
    photoEntity.addStringProperty("date")
    photoEntity.addStringProperty("album")
    photoEntity.addStringProperty("uri")

    DaoGenerator().generateAll(schema, "./app/src/main/java")

    Property
}