package org.chewing.v1.implementation.media

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalFileClient
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.springframework.stereotype.Component

@Component
class FileAppender(
    private val externalFileClient: ExternalFileClient,
) {
    suspend fun appendFile(file: FileData, media: Media) {
        try {
            externalFileClient.uploadFile(file, media)
        } catch (e: Exception) {
            throw ConflictException(ErrorCode.FILE_UPLOAD_FAILED)
        }
    }
}
