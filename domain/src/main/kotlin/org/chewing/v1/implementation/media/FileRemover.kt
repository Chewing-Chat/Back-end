package org.chewing.v1.implementation.media

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalFileClient
import org.chewing.v1.model.media.Media
import org.springframework.stereotype.Component

@Component
class FileRemover(
    private val externalFileClient: ExternalFileClient,
) {
    suspend fun removeFile(media: Media) {
        try {
            externalFileClient.removeFile(media)
        } catch (e: Exception) {
            throw ConflictException(ErrorCode.FILE_DELETE_FAILED)
        }
    }
}
