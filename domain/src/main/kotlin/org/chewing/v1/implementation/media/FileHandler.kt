package org.chewing.v1.implementation.media

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.util.AsyncJobExecutor
import org.springframework.stereotype.Component

@Component
class FileHandler(
    private val fileAppender: FileAppender,
    private val fileRemover: FileRemover,
    private val fileGenerator: FileGenerator,
    private val fileValidator: FileValidator,
    private val asyncJobExecutor: AsyncJobExecutor,
) {
    fun handleNewFiles(userId: String, files: List<FileData>, category: FileCategory): List<Media> {
        fileValidator.validateFilesNameCorrect(files)
        val mediaWithFiles = fileGenerator.generateMedias(files, userId, category)
        asyncJobExecutor.executeAsyncJobs(mediaWithFiles) { (file, media) ->
            fileAppender.appendFile(file, media)
        }
        return mediaWithFiles.map { it.second }
    }

    fun handleNewFile(userId: String, file: FileData, category: FileCategory): Media {
        fileValidator.validateFileNameCorrect(file)
        val media = fileGenerator.generateMedia(file, userId, category)
        asyncJobExecutor.executeAsyncJob(media) {
            fileAppender.appendFile(file, media)
        }
        return media
    }

    fun handleOldFile(media: Media) {
        if (media.type != MediaType.IMAGE_BASIC) {
            asyncJobExecutor.executeAsyncJob(media) {
                fileRemover.removeFile(media)
            }
        }
    }

    fun handleOldFiles(medias: List<Media>) {
        asyncJobExecutor.executeAsyncJobs(medias) { media ->
            fileRemover.removeFile(media)
        }
    }
}
