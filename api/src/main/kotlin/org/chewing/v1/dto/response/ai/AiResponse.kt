package org.chewing.v1.dto.response.ai

data class AiResponse(
    val summary: String,
) {
    companion object {
        fun from(summary: String): AiResponse = AiResponse(summary)
    }
}
