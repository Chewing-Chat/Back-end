package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

class ChatBombMessage private constructor(
    val messageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    override val type: MessageType = MessageType.BOMB,
    val text: String,
    val expiredAt: LocalDateTime,
) : ChatMessage() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: String,
            timestamp: LocalDateTime,
            number: ChatNumber,
            text: String,
            expiredAt: LocalDateTime,
        ): ChatBombMessage {
            return ChatBombMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
                expiredAt = expiredAt,
                text = text,
            )
        }
    }
}
