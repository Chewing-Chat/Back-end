package org.chewing.v1.implementation.media

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.media.FileData
import org.springframework.stereotype.Component

@Component
class FileValidator() {
    fun validateFilesNameCorrect(files: List<FileData>) {
        files.forEachIndexed { index, file ->
            val expectedFileName = "$index."
            if (!file.name.startsWith(expectedFileName)) {
                throw ConflictException(ErrorCode.FILE_NAME_INCORRECT)
            }
        }
    }
    fun validateFileNameCorrect(file: FileData) {
        if (!file.name.startsWith("0.")) {
            throw ConflictException(ErrorCode.FILE_NAME_INCORRECT)
        }
    }
}
