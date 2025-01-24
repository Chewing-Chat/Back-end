package org.chewing.v1.external

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.apache.http.entity.ContentType
import org.chewing.v1.client.OpenAiClient
import org.chewing.v1.config.OpenAiConfig
import org.chewing.v1.dto.ChatGPTRequest
import org.chewing.v1.dto.ChatGPTResponse
import org.chewing.v1.model.ai.TextPrompt
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

class ExternalAiClientTest {

    companion object {
        private lateinit var mockWebServer: MockWebServer

        @BeforeAll
        @JvmStatic
        fun setup() {
            mockWebServer = MockWebServer()
            mockWebServer.start()
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            mockWebServer.shutdown()
        }
    }

    val objectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    }

    private val mockServerUrl = mockWebServer.url("/").toString().removeSuffix("/")
    private val testApiKey = "testApiKey"

    private val openAiConfig: OpenAiConfig = OpenAiConfig(
        openAiKey = testApiKey,
        openAiUrl = mockServerUrl,
    )
    private val webClient: WebClient = openAiConfig.openAiWebClient()
    private val testModel = "testModel"
    private val openAiClient: OpenAiClient = OpenAiClient(
        webClient,
    )
    private val externalAiClientImpl = ExternalAiClientImpl(
        testModel,
        openAiClient,
    )

    @Test
    fun `프롬프트 테스트`() = runBlocking {
        val prompt = TextPrompt.of("test")

        val response = ChatGPTResponse(
            choices = listOf(
                ChatGPTResponse.Choice(
                    index = 0,
                    message = ChatGPTResponse.Choice.ChatGptMessage(
                        content = "테스트 응답",
                        role = "assistant",
                    ),
                ),
            ),
        )

        val jsonResponse = objectMapper.writeValueAsString(response)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(jsonResponse),
        )

        val result = externalAiClientImpl.prompt(listOf(prompt))

        assert(result == response.choices[0].message.content)

        val recordedRequest = mockWebServer.takeRequest()

        assert(recordedRequest.method == "POST")

        assert(recordedRequest.path == "/")

        val headers = recordedRequest.headers
        assert(headers["Authorization"] == "Bearer $testApiKey")
        assert(headers["Content-Type"] == ContentType.APPLICATION_JSON.mimeType)

        val requestBody = recordedRequest.body.readUtf8()
        val expectedRequestBody = objectMapper.writeValueAsString(
            ChatGPTRequest.of(
                model = testModel,
                prompts = listOf(prompt),
            ),
        )
        val expectedJsonNode = objectMapper.readTree(expectedRequestBody)
        val actualJsonNode = objectMapper.readTree(requestBody)

        assert(expectedJsonNode == actualJsonNode)
    }

    @Test
    fun `프롬프트 테스트 - 실패`() = runBlocking {
        val prompt = TextPrompt.of("test")

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"error\": \"Internal Server Error\"}"),
        )

        val result = externalAiClientImpl.prompt(listOf(prompt))

        assert(result == null)

        val recordedRequest = mockWebServer.takeRequest()

        assert(recordedRequest.method == "POST")

        assert(recordedRequest.path == "/")

        val headers = recordedRequest.headers
        assert(headers["Authorization"] == "Bearer $testApiKey")
        assert(headers["Content-Type"] == ContentType.APPLICATION_JSON.mimeType)

        val requestBody = recordedRequest.body.readUtf8()
        val expectedRequestBody = objectMapper.writeValueAsString(
            ChatGPTRequest.of(
                model = testModel,
                prompts = listOf(prompt),
            ),
        )
        val expectedJsonNode = objectMapper.readTree(expectedRequestBody)
        val actualJsonNode = objectMapper.readTree(requestBody)

        assert(expectedJsonNode == actualJsonNode)
    }
}
