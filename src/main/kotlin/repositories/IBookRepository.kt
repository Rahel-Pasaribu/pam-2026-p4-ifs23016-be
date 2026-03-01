package org.delcom.repositories

import org.delcom.entities.Book

interface IBookRepository {

    suspend fun getBooks(search: String): List<Book>

    suspend fun getBookById(id: String): Book?

    suspend fun getBookByTitle(title: String): Book?

    suspend fun addBook(book: Book): String

    suspend fun updateBook(id: String, newBook: Book): Boolean

    suspend fun removeBook(id: String): Boolean
}