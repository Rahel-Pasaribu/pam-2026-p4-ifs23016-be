package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.BookRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IBookRepository
import java.io.File
import java.util.*

class BookService(private val bookRepository: IBookRepository) {

    suspend fun getAllBooks(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val books = bookRepository.getBooks(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar book",
            mapOf(Pair("books", books))
        )
        call.respond(response)
    }

    suspend fun getBookById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID book tidak boleh kosong!")

        val book = bookRepository.getBookById(id)
            ?: throw AppException(404, "Data book tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data book",
            mapOf(Pair("book", book))
        )
        call.respond(response)
    }

    private suspend fun getBookRequest(call: ApplicationCall): BookRequest {
        val bookReq = BookRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {

                is PartData.FormItem -> {
                    when (part.name) {
                        "title" -> bookReq.title = part.value.trim()
                        "description" -> bookReq.description = part.value
                        "genre" -> bookReq.genre = part.value
                        "mainCharacter" -> bookReq.mainCharacter = part.value
                        "author" -> bookReq.author = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/books/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    bookReq.coverPath = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return bookReq
    }

    private fun validateBookRequest(bookReq: BookRequest) {
        val validatorHelper = ValidatorHelper(bookReq.toMap())

        validatorHelper.required("title", "Title tidak boleh kosong")
        validatorHelper.required("description", "Description tidak boleh kosong")
        validatorHelper.required("genre", "Genre tidak boleh kosong")
        validatorHelper.required("mainCharacter", "Main Character tidak boleh kosong")
        validatorHelper.required("author", "Author tidak boleh kosong")
        validatorHelper.required("coverPath", "Cover tidak boleh kosong")

        validatorHelper.validate()

        val file = File(bookReq.coverPath)
        if (!file.exists()) {
            throw AppException(400, "Cover book gagal diupload!")
        }
    }

    suspend fun createBook(call: ApplicationCall) {

        val bookReq = getBookRequest(call)

        validateBookRequest(bookReq)

        val existBook = bookRepository.getBookByTitle(bookReq.title)
        if (existBook != null) {

            val tmpFile = File(bookReq.coverPath)
            if (tmpFile.exists()) {
                tmpFile.delete()
            }

            throw AppException(409, "Book dengan title ini sudah terdaftar!")
        }

        val bookId = bookRepository.addBook(
            bookReq.toEntity()
        )

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data book",
            mapOf(Pair("bookId", bookId))
        )
        call.respond(response)
    }

    suspend fun updateBook(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID book tidak boleh kosong!")

        val oldBook = bookRepository.getBookById(id)
            ?: throw AppException(404, "Data book tidak tersedia!")

        val bookReq = getBookRequest(call)

        if (bookReq.coverPath.isEmpty()) {
            bookReq.coverPath = oldBook.coverPath
        }

        validateBookRequest(bookReq)

        if (bookReq.title != oldBook.title) {
            val existBook = bookRepository.getBookByTitle(bookReq.title)
            if (existBook != null) {

                val tmpFile = File(bookReq.coverPath)
                if (tmpFile.exists()) {
                    tmpFile.delete()
                }

                throw AppException(409, "Book dengan title ini sudah terdaftar!")
            }
        }

        if (bookReq.coverPath != oldBook.coverPath) {
            val oldFile = File(oldBook.coverPath)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val isUpdated = bookRepository.updateBook(
            id, bookReq.toEntity()
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data book!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data book",
            null
        )
        call.respond(response)
    }

    suspend fun deleteBook(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID book tidak boleh kosong!")

        val oldBook = bookRepository.getBookById(id)
            ?: throw AppException(404, "Data book tidak tersedia!")

        val oldFile = File(oldBook.coverPath)

        val isDeleted = bookRepository.removeBook(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data book!")
        }

        if (oldFile.exists()) {
            oldFile.delete()
        }

        val response = DataResponse(
            "success",
            "Berhasil menghapus data book",
            null
        )
        call.respond(response)
    }

    suspend fun getBookImage(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val book = bookRepository.getBookById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(book.coverPath)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}