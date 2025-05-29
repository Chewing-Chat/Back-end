package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.message.ChatAiMessage

data class PairAiChatMessageResponse(
    val userMessage: AiChatMessageResponse,
    val aiMessage: AiChatMessageResponse,
) {
    companion object {
        fun of(
            userMessage: ChatAiMessage,
            aiMessage: ChatAiMessage,
        ): PairAiChatMessageResponse {
            return PairAiChatMessageResponse(
                userMessage = AiChatMessageResponse.of(
                    userMessage,
                ),
                aiMessage = AiChatMessageResponse.of(
                    aiMessage,
                ),
            )
        }
    }
}
