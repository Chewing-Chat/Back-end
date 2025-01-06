package org.chewing.v1.external

import org.chewing.v1.dto.ChatMessageDto
import org.chewing.v1.model.chat.message.ChatMessage
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class ExternalChatNotificationClientImpl(
    private val messagingTemplate: SimpMessagingTemplate,
) : ExternalChatNotificationClient {
    override fun sendMessage(chatMessage: ChatMessage, userId: String) {
        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/chat",
            ChatMessageDto.from(chatMessage),
        )
    }
}
