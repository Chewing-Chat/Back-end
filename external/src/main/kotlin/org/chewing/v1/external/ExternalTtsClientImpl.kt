package org.chewing.v1.external

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

@Component
class ExternalTtsClientImpl(
    private val externalFileClient: ExternalFileClient,
    @Value("\${ncp.tts.url}") private val apiUrl: String,
    @Value("\${ncp.tts.accessKey}") private val clientId: String,
    @Value("\${ncp.tts.secretKey}") private val clientSecret: String,
    @Value("\${ncp.storage.bucketName}") private val bucketName: String,
    @Value("\${ncp.storage.endpoint}") private val baseUrl: String,
) : ExternalTtsClient {

    override suspend fun generateTts(text: String, speaker: String): Media {
        try {
            println("Client ID: $clientId") // 디버깅용 출력
            println("Client Secret: $clientSecret") // 디버깅용 출력
            println("API URL: $apiUrl") // 디버깅용 출력

            val encodedText = URLEncoder.encode(text, "UTF-8")
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId)
            connection.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret)
            connection.doOutput = true

            val postParams = "speaker=$speaker&volume=0&speed=0&pitch=0&format=mp3&text=$encodedText"
            println("Post Params: $postParams") // 요청 파라미터 디버깅
            DataOutputStream(connection.outputStream).use { outputStream ->
                outputStream.writeBytes(postParams)
                outputStream.flush()
            }

            val responseCode = connection.responseCode
            return if (responseCode == 200) {
                val tempFileName = "${System.currentTimeMillis() / 1000}.mp3" // 초 단위로 줄임
                val tempFile = File(tempFileName)
                tempFile.createNewFile()

                connection.inputStream.use { inputStream ->
                    FileOutputStream(tempFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }

                // Media 객체 생성
                val media = Media.upload(
                    baseUrl = baseUrl,
                    buckName = bucketName,
                    category = FileCategory.TTS,
                    userId = "testUserId",
                    fileName = tempFile.name,
                    type = MediaType.AUDIO_MP3,
                )

                // 파일 업로드
                // FileData 인스턴스를 생성할 때 'of' 메서드를 사용
                val fileData = FileData.of(
                    inputStream = tempFile.inputStream(),
                    contentType = MediaType.AUDIO_MP3,
                    fileName = tempFile.name,
                    size = tempFile.length(),
                )
                externalFileClient.uploadFile(fileData, media)

                // 업로드 후 로컬 임시 파일 삭제
                tempFile.delete()

                println("Uploaded file URL: ${media.url}")
                media
            } else {
                val errorMessage = connection.errorStream.bufferedReader().use { it.readText() }
                throw IOException("TTS API Error: $errorMessage")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to generate TTS: ${e.message}", e)
        }
    }
}
