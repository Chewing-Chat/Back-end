package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

class ChatReadMessage private constructor(
    override val chatRoomId: String,
    override val type: MessageType = MessageType.READ,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
) : ChatMessage() {
    companion object {
        fun of(
            chatRoomId: String,
            senderId: String,
            timestamp: LocalDateTime,
            number: ChatNumber,
        ): ChatReadMessage {
            return ChatReadMessage(
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
            )
        }
    }
}
