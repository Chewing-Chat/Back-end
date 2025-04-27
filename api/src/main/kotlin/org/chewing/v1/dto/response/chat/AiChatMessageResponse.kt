package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.message.ChatAiMessage

data class AiChatMessageResponse(
    val messageId: String,
    val type: String,
    val senderId: String,
    val timestamp: String,
    val seqNumber: Int,
    val text: String,
    val senderType: String
) {
    companion object {
        fun of(promptMessage: ChatAiMessage): AiChatMessageResponse {
            return AiChatMessageResponse(
                messageId = promptMessage.messageId,
                type = promptMessage.type.name.lowercase(),
                senderId = promptMessage.senderId.id,
                timestamp = promptMessage.timestamp.toString(),
                seqNumber = promptMessage.roomSequence.sequence,
                text = promptMessage.text,
                senderType = promptMessage.senderType.name.lowercase()
            )
        }
    }
}
