package org.chewing.v1.external

import org.chewing.v1.dto.ChatMessageDto
import org.chewing.v1.dto.OwnedChatMessageDto
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class ExternalChatNotificationClientImpl(
    private val messagingTemplate: SimpMessagingTemplate,
) : ExternalChatNotificationClient {
    override fun sendMessage(chatMessage: ChatMessage, userId: UserId) {
        when(chatMessage.chatRoomType) {
            ChatRoomType.GROUP -> {
                messagingTemplate.convertAndSend(
                    "/queue/chat/group",
                    ChatMessageDto.from(chatMessage),
                )
            }
            ChatRoomType.DIRECT -> {
                messagingTemplate.convertAndSendToUser(
                    userId.id,
                    "/queue/chat/direct",
                    ChatMessageDto.from(chatMessage),
                )
            }
        }
    }

    override fun sendOwnedMessage(chatMessage: ChatMessage) {
        messagingTemplate.convertAndSendToUser(
            chatMessage.senderId.id,
            "/queue/chat",
            OwnedChatMessageDto.from(chatMessage),
        )
    }
}
