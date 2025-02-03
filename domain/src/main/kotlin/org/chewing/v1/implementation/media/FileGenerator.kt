package org.chewing.v1.implementation.media

import org.chewing.v1.model.media.*
import org.chewing.v1.model.user.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FileGenerator(
    @Value("\${ncp.storage.endpoint}") private val baseUrl: String,
    @Value("\${ncp.storage.bucketName}") private val bucketName: String,
) {
    fun generateMedias(
        files: List<FileData>,
        userId: UserId,
        category: FileCategory,
    ): List<Pair<FileData, Media>> = files.map { file ->
        Pair(file, Media.upload(baseUrl, bucketName, category, userId, file.name, file.contentType))
    }

    fun generateMedia(
        file: FileData,
        userId: UserId,
        category: FileCategory,
    ): Media = Media.upload(baseUrl, bucketName, category, userId, file.name, file.contentType)
}
