package org.delcom.dao

import org.delcom.tables.BookTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class BookDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, BookDAO>(BookTable)

    var title by BookTable.title
    var coverPath by BookTable.coverPath
    var description by BookTable.description
    var genre by BookTable.genre
    var mainCharacter by BookTable.mainCharacter
    var author by BookTable.author
    var createdAt by BookTable.createdAt
    var updatedAt by BookTable.updatedAt
}