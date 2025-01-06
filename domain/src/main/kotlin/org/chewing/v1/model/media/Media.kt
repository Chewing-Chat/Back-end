package org.chewing.v1.model.media

import java.util.*

class Media private constructor(
    val category: FileCategory,
    val url: String,
    val type: MediaType,
    val index: Int,
    val path: String,
) {
    companion object {
        fun upload(
            baseUrl: String,
            buckName: String,
            category: FileCategory,
            userId: String,
            fileName: String,
            type: MediaType,
        ): Media {
            val randomId = UUID.randomUUID().toString()
            val basePath = "$baseUrl/$buckName"
            val filePath = "${category.name}/$userId/$randomId/$fileName"
            return Media(category, "$basePath/$filePath", type, fileName.split(".")[0].toInt(), filePath)
        }

        fun of(category: FileCategory, imagePath: String, index: Int, type: MediaType): Media =
            Media(category, imagePath, type, index, "")
    }
}
