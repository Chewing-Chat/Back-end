package org.chewing.v1.external

import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media

interface ExternalFileClient {
    suspend fun uploadFile(file: FileData, media: Media)
    suspend fun removeFile(media: Media)
}
