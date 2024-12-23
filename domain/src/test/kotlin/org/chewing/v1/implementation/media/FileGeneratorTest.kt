package org.chewing.v1.implementation.media

import org.chewing.v1.TestDataFactory
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.MediaType
import org.junit.jupiter.api.Test

class FileGeneratorTest {
    private val baseUrl = "baseUrl"
    private val bucketName = "bucketName"
    private val fileGenerator = FileGenerator(
        baseUrl = baseUrl,
        bucketName = bucketName,
    )

    @Test
    fun `피드 파일 목록 생성`() {
        val userId = "userId"
        val files = listOf(
            TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png"),
        )
        val result = fileGenerator.generateMedias(files, userId, FileCategory.FEED)

        assert(result.size == 1)
        assert(result[0].first == files[0])
        assert(result[0].second.url.startsWith("$baseUrl/$bucketName/FEED/$userId"))
        assert(result[0].second.index == 0)
        assert(result[0].second.type == MediaType.IMAGE_PNG)
        assert(result[0].second.category == FileCategory.FEED)
        assert(result[0].second.url.endsWith("0.png"))
    }

    @Test
    fun `피드 파일 생성`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png")
        val result = fileGenerator.generateMedia(file, userId, FileCategory.FEED)

        assert(result.url.startsWith("$baseUrl/$bucketName/FEED/$userId"))
        assert(result.index == 0)
        assert(result.type == MediaType.IMAGE_PNG)
        assert(result.category == FileCategory.FEED)
        assert(result.url.endsWith("0.png"))
    }

    @Test
    fun `유저 프로필 이미지 파일 생성`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png")
        val result = fileGenerator.generateMedia(file, userId, FileCategory.PROFILE)

        assert(result.url.startsWith("$baseUrl/$bucketName/PROFILE/$userId"))
        assert(result.index == 0)
        assert(result.type == MediaType.IMAGE_PNG)
        assert(result.category == FileCategory.PROFILE)
        assert(result.url.endsWith("0.png"))
    }

    @Test
    fun `유저 배경 이미지 파일 생성`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.IMAGE_PNG, "0.png")
        val result = fileGenerator.generateMedia(file, userId, FileCategory.BACKGROUND)

        assert(result.url.startsWith("$baseUrl/$bucketName/BACKGROUND/$userId"))
        assert(result.index == 0)
        assert(result.type == MediaType.IMAGE_PNG)
        assert(result.category == FileCategory.BACKGROUND)
        assert(result.url.endsWith("0.png"))
    }

    @Test
    fun `유저 TTS 파일 생성`() {
        val userId = "userId"
        val file = TestDataFactory.createFileData(MediaType.AUDIO_MP3, "0.mp3")
        val result = fileGenerator.generateMedia(file, userId, FileCategory.TTS)

        assert(result.url.startsWith("$baseUrl/$bucketName/TTS/$userId"))
        assert(result.index == 0)
        assert(result.type == MediaType.AUDIO_MP3)
        assert(result.category == FileCategory.TTS)
        assert(result.url.endsWith("0.mp3"))
    }
}
