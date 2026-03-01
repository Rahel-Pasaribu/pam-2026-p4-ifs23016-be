package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.PlantDAO
import org.delcom.entities.Plant
import org.delcom.dao.BookDAO
import org.delcom.entities.Book
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)

fun daoToModel(dao: BookDAO) = Book(
    id = dao.id.value.toString(),
    title = dao.title,
    coverPath = dao.coverPath,
    description = dao.description,
    genre = dao.genre,
    mainCharacter = dao.mainCharacter,
    author = dao.author,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)