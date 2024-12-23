package org.chewing.v1.implementation.media

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalFileClient
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.util.AsyncJobExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class FileHandlerTest {
    private val externalFileClient: ExternalFileClient = mockk()
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val asyncJobExecutor = AsyncJobExecutor(ioScope)

    private val fileAppender = FileAppender(externalFileClient)
    private val fileRemover = FileRemover(externalFileClient)
    private val baseUrl = "baseUrl"
    private val bucketName = "bucketName"
    private val fileGenerator = FileGenerator(
        baseUrl = baseUrl,
        bucketName = bucketName,
    )
    private val fileValidator = FileValidator()

    private val fileHandler = FileHandler(fileAppender, fileRemover, fileGenerator, fileValidator, asyncJobExecutor)

    @Test
    fun `파일 목록 생성 테스트 - 실패(파일 이름이 올바르지 않음)`() {
        val userId = "userId"
        val files = listOf(
            TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png"),
            TestDataFactory.createFileData(MediaType.VIDEO_MP4, "4.mp4"),
        )
        val exception = assertThrows<ConflictException> {
            fileHandler.handleNewFiles(userId, files, FileCategory.FEED)
        }
        assert(exception.errorCode == ErrorCode.FILE_NAME_INCORRECT)
    }

    @Test
    fun `파일 목록 전송시 하나라도 실패한다면 Exception을 던져야함`() {
        val userId = "userId"
        val files = listOf(
            TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png"),
            TestDataFactory.createFileData(MediaType.IMAGE_PNG, "1.png"),
        )

        coEvery { externalFileClient.uploadFile(eq(files[0]), any()) } throws RuntimeException()

        val exception = assertThrows<ConflictException> {
            fileHandler.handleNewFiles(userId, files, FileCategory.FEED)
        }

        assert(exception.errorCode == ErrorCode.FILE_UPLOAD_FAILED)
    }

    @Test
    fun `파일목록 생성 테스트 - 성공`() {
        val userId = "userId"

        val files = listOf(
            TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png"),
            TestDataFactory.createFileData(MediaType.IMAGE_PNG, "1.png"),
        )

        coJustRun { externalFileClient.uploadFile(any(), any()) }

        fileHandler.handleNewFiles(userId, files, FileCategory.FEED)

        coVerify(exactly = files.size) { externalFileClient.uploadFile(any(), any()) }
    }

    @Test
    fun `파일 생성 테스트 - 실패`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.IMAGE_PNG, "1.png")

        val exception = assertThrows<ConflictException> {
            fileHandler.handleNewFile(userId, file, FileCategory.FEED)
        }
        assert(exception.errorCode == ErrorCode.FILE_NAME_INCORRECT)
    }

    @Test
    fun `파일 전송 실패시 Exception을 던져야함`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png")

        coEvery { externalFileClient.uploadFile(eq(file), any()) } throws RuntimeException()

        val exception = assertThrows<ConflictException> {
            fileHandler.handleNewFile(userId, file, FileCategory.FEED)
        }

        assert(exception.errorCode == ErrorCode.FILE_UPLOAD_FAILED)
    }

    @Test
    fun `파일 생성 테스트 - 성공`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png")

        coEvery { externalFileClient.uploadFile(any(), any()) } just Runs

        fileHandler.handleNewFile(userId, file, FileCategory.FEED)
    }

    @Test
    fun `파일 삭제 테스트 - 성공`() {
        val media = TestDataFactory.createMedia(FileCategory.PROFILE, 0, MediaType.IMAGE_PNG)

        coEvery { externalFileClient.removeFile(media) } just Runs

        assertDoesNotThrow {
            fileHandler.handleOldFile(media)
        }
    }

    @Test
    fun `파일 목록 삭제 테스트 - 실패`() {
        val media1 = TestDataFactory.createMedia(FileCategory.FEED, 0, MediaType.IMAGE_PNG)
        val media2 = TestDataFactory.createMedia(FileCategory.FEED, 0, MediaType.IMAGE_PNG)

        coEvery { externalFileClient.removeFile(media1) } throws RuntimeException()

        val exception = assertThrows<ConflictException> {
            fileHandler.handleOldFiles(listOf(media1, media2))
        }

        assert(exception.errorCode == ErrorCode.FILE_DELETE_FAILED)
    }

    @Test
    fun `파일 목록 삭제 테스트 성공`() {
        val media1 = TestDataFactory.createMedia(FileCategory.FEED, 0, MediaType.IMAGE_PNG)
        val media2 = TestDataFactory.createMedia(FileCategory.FEED, 0, MediaType.IMAGE_PNG)

        coJustRun { externalFileClient.removeFile(any()) }

        assertDoesNotThrow {
            fileHandler.handleOldFiles(listOf(media1, media2))
        }
    }

    @Test
    fun `기본 파일 이라면 삭제를 하지 않음`() {
        // Given
        val media = TestDataFactory.createMedia(FileCategory.PROFILE, 0, MediaType.IMAGE_BASIC)

        // When & Then
        assertDoesNotThrow {
            fileHandler.handleOldFile(media)
        }

        // Then
        coVerify(exactly = 0) { externalFileClient.removeFile(any()) }
        confirmVerified(externalFileClient)
    }
}
