package org.chewing.v1.external

import mu.KotlinLogging
import org.chewing.v1.client.OpenAiClient
import org.chewing.v1.dto.ChatGPTRequest
import org.chewing.v1.model.ai.Prompt
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ExternalAiClientImpl(
    @Value("\${openai.model}") private val model: String,
    private val openAiClient: OpenAiClient,
) : ExternalAiClient {

    private val logger = KotlinLogging.logger {}

    override suspend fun prompt(prompts: List<Prompt>): String? {
        val request = ChatGPTRequest.of(model, prompts)
        return try {
            val responseBody = openAiClient.execute(request)
            responseBody!!.choices[0].message.content.trim()
        } catch (e: Exception) {
            logger.error("API 호출 실패: ${e.message}", e)
            null
        }
    }
}
