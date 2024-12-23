package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

class ChatDeleteMessage private constructor(
    val targetMessageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    override val type: MessageType = MessageType.DELETE,
) : ChatMessage() {

    companion object {
        fun of(
            targetMessageId: String,
            chatRoomId: String,
            senderId: String,
            timestamp: LocalDateTime,
            number: ChatNumber,
        ): ChatDeleteMessage {
            return ChatDeleteMessage(
                targetMessageId = targetMessageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
            )
        }
    }
}
