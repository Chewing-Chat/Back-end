package org.chewing.v1.external

import org.chewing.v1.dto.ChatMessageDto
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.user.UserId
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class ExternalChatNotificationClientImpl(
    private val messagingTemplate: SimpMessagingTemplate,
) : ExternalChatNotificationClient {
    override fun sendMessage(chatMessage: ChatMessage, userId: UserId) {
        messagingTemplate.convertAndSendToUser(
            userId.id,
            "/queue/chat",
            ChatMessageDto.from(chatMessage),
        )
    }
}
