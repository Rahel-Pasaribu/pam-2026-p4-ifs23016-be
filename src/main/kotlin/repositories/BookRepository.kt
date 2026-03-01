package org.delcom.repositories

import org.delcom.dao.BookDAO
import org.delcom.entities.Book
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.BookTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class BookRepository : IBookRepository {

    override suspend fun getBooks(search: String): List<Book> = suspendTransaction {
        if (search.isBlank()) {
            BookDAO.all()
                .orderBy(BookTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            BookDAO
                .find {
                    BookTable.title.lowerCase() like keyword
                }
                .orderBy(BookTable.title to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getBookById(id: String): Book? = suspendTransaction {
        BookDAO
            .find { BookTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getBookByTitle(title: String): Book? = suspendTransaction {
        BookDAO
            .find { BookTable.title eq title }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addBook(book: Book): String = suspendTransaction {
        val bookDAO = BookDAO.new {
            this.title = book.title
            this.coverPath = book.coverPath
            this.description = book.description
            this.genre = book.genre
            this.mainCharacter = book.mainCharacter
            this.author = book.author
            this.createdAt = book.createdAt
            this.updatedAt = book.updatedAt
        }

        bookDAO.id.value.toString()
    }

    override suspend fun updateBook(id: String, newBook: Book): Boolean = suspendTransaction {
        val bookDAO = BookDAO
            .find { BookTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (bookDAO != null) {
            bookDAO.title = newBook.title
            bookDAO.coverPath = newBook.coverPath
            bookDAO.description = newBook.description
            bookDAO.genre = newBook.genre
            bookDAO.mainCharacter = newBook.mainCharacter
            bookDAO.author = newBook.author
            bookDAO.updatedAt = newBook.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeBook(id: String): Boolean = suspendTransaction {
        val rowsDeleted = BookTable.deleteWhere {
            BookTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}