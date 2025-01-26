package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatReadMessage private constructor(
    override val chatRoomId: String,
    override val type: MessageType = MessageType.READ,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatLogSequence,
) : ChatMessage() {
    companion object {
        fun of(
            chatRoomId: String,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatLogSequence,
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
