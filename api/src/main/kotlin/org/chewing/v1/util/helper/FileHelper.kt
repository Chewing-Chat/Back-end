package org.chewing.v1.util.helper

import net.coobird.thumbnailator.Thumbnails
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.MediaType
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

object FileHelper {
    @Throws(IOException::class, ConflictException::class)
    fun convertMultipartFileToFileData(file: MultipartFile): FileData {
        val originalFilename = file.originalFilename ?: throw ConflictException(ErrorCode.FILE_NAME_COULD_NOT_EMPTY)
        val contentType = file.contentType ?: throw ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE)
        val mediaType = MediaType.fromType(contentType) ?: throw ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE)

        var inputStreamToUse = file.inputStream
        var optimizedSize = file.size

        if (mediaType.name.startsWith("IMAGE")) {
            val originalImage = ImageIO.read(file.inputStream)
            if (originalImage != null) {
                val baos = ByteArrayOutputStream()
                Thumbnails.of(originalImage)
                    .outputFormat("jpg")
                    .toOutputStream(baos)
                val optimizedBytes = baos.toByteArray()
                inputStreamToUse = ByteArrayInputStream(optimizedBytes)
                optimizedSize = optimizedBytes.size.toLong()
            }
        }

        return FileData.of(
            inputStreamToUse,
            MediaType.IMAGE_JPG,
            originalFilename,
            optimizedSize,
        )
    }

    @Throws(IOException::class, AuthorizationException::class)
    fun convertMultipartFileToFileDataList(files: List<MultipartFile>): List<FileData> = files.map { convertMultipartFileToFileData(it) }
}
