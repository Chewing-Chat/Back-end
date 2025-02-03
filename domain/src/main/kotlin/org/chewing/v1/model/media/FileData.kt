package org.chewing.v1.model.media

import java.io.InputStream

class FileData private constructor(
    val inputStream: InputStream,
    val contentType: MediaType,
    val name: String,
    val size: Long,
) {
    companion object {
        fun of(
            inputStream: InputStream,
            contentType: MediaType,
            fileName: String,
            size: Long,
        ): FileData {
            return FileData(inputStream, contentType, fileName, size)
        }
    }
}
