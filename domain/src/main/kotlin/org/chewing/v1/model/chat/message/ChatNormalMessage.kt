package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

class ChatNormalMessage private constructor(
    val messageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    val text: String,
) : ChatMessage() {
    override val type: MessageType = MessageType.NORMAL

    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: String,
            text: String,
            number: ChatNumber,
            timestamp: LocalDateTime,
        ): ChatNormalMessage {
            return ChatNormalMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                text = text,
                number = number,
                timestamp = timestamp,
            )
        }
    }
}
