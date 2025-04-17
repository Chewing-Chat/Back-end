package org.chewing.v1.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.chewing.v1.model.ai.ImagePrompt
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.ai.TextPrompt

data class ChatGPTRequest(
    val model: String,
    val messages: List<Message>,
    @JsonProperty("max_tokens") val maxTokens: Int = 150,
) {
    companion object {
        fun of(
            model: String,
            prompts: List<Prompt>,
        ): ChatGPTRequest {
            return ChatGPTRequest(
                model = model,
                messages = prompts.map { Message(content = listOf(Message.of(it))) },
            )
        }
    }

    data class Message(
        val role: String = "user",
        val content: List<Any>,
    ) {
        companion object {
            fun of(prompt: Prompt): Any = when (prompt) {
                is TextPrompt -> ContentText.of(prompt)
                is ImagePrompt -> ContentImage.of(prompt)
            }
        }

        data class ContentText(
            val type: String = "text",
            val text: String,
        ) {
            companion object {
                fun of(prompt: TextPrompt): ContentText = ContentText(text = prompt.text)
            }
        }

        data class ContentImage(
            val type: String = "image_url",
            @JsonProperty("image_url") val imageUrl: Url,
        ) {
            companion object {
                fun of(prompt: ImagePrompt): ContentImage = ContentImage(imageUrl = Url(prompt.imageUrl))
            }

            data class Url(
                val url: String,
            )
        }
    }
}
