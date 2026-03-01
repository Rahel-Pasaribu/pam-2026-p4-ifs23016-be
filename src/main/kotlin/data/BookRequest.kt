package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Book

@Serializable
data class BookRequest(
    var title: String = "",
    var description: String = "",
    var genre: String = "",
    var mainCharacter: String = "",
    var author: String = "",
    var coverPath: String = "",
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "description" to description,
            "genre" to genre,
            "mainCharacter" to mainCharacter,
            "author" to author,
            "coverPath" to coverPath
        )
    }

    fun toEntity(): Book {
        return Book(
            title = title,
            description = description,
            genre = genre,
            mainCharacter = mainCharacter,
            author = author,
            coverPath = coverPath,
        )
    }
}