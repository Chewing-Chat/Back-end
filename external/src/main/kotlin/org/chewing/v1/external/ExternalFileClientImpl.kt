package org.chewing.v1.external

import mu.KotlinLogging
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.concurrent.Executors

@Component
class ExternalFileClientImpl(
    private val s3AsyncClient: S3AsyncClient,
    @Value("\${ncp.storage.bucketName}")
    private val bucketName: String,
) : ExternalFileClient {

    private val logger = KotlinLogging.logger {}

    override suspend fun uploadFile(file: FileData, media: Media) {
        val executor = Executors.newFixedThreadPool(5)
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(media.path)
            .contentType(media.type.type)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
        val requestBody = AsyncRequestBody.fromInputStream { builder ->
            builder.inputStream(file.inputStream)
            builder.contentLength(file.size)
            builder.executor(executor)
        }
        try {
            try {
                s3AsyncClient.putObject(putObjectRequest, requestBody).join()
            } catch (exception: Exception) {
                logger.error { "Failed to upload file: ${exception.message}" }
                throw exception
            }
        } finally {
            executor.shutdown()
        }
    }

    override suspend fun removeFile(media: Media) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(media.url)
            .build()
        s3AsyncClient.deleteObject(deleteObjectRequest)
    }
}
