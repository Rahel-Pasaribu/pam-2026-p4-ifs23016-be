package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object BookTable : UUIDTable("books") {

    val title = varchar("title", 255)
    val coverPath = varchar("cover_path", 255)
    val description = text("description")
    val genre = varchar("genre", 100)
    val mainCharacter = varchar("main_character", 100)
    val author = varchar("author", 100)

    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}