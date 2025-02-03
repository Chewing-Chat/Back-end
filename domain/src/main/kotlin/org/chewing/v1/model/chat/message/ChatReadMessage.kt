package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatReadMessage private constructor(
    override val chatRoomId: ChatRoomId,
    override val type: MessageType = MessageType.READ,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatSequence,
) : ChatMessage() {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatSequence,
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
