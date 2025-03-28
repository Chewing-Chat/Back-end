package org.chewing.v1.util.helper

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
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
        val originalFilename = file.originalFilename
            ?: throw ConflictException(ErrorCode.FILE_NAME_COULD_NOT_EMPTY)
        val contentType = file.contentType
            ?: throw ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE)
        val mediaType = MediaType.fromType(contentType)
            ?: throw ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE)

        var inputStreamToUse = file.inputStream
        var optimizedSize = file.size
        var outputFilename = originalFilename
        var outputMediaType = mediaType

        if (mediaType.name.startsWith("IMAGE")) {
            // scrimage 라이브러리로 이미지를 WebP로 변환 (WebpWriter.DEFAULT 사용)
            val image = ImmutableImage.loader().fromStream(file.inputStream)
            val optimizedBytes = image.bytes(WebpWriter.DEFAULT)
            inputStreamToUse = ByteArrayInputStream(optimizedBytes)
            optimizedSize = optimizedBytes.size.toLong()

            // 파일명과 MediaType을 WebP로 업데이트
            outputFilename = originalFilename.substringBeforeLast('.') + ".webp"
            outputMediaType = MediaType.IMAGE_WEBP
        }


        return FileData.of(
            inputStreamToUse,
            mediaType,
            originalFilename,
            optimizedSize,
        )
    }

    @Throws(IOException::class, AuthorizationException::class)
    fun convertMultipartFileToFileDataList(files: List<MultipartFile>): List<FileData> = files.map { convertMultipartFileToFileData(it) }
}
