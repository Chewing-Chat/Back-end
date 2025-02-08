package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatReadMessage private constructor(
    override val chatRoomId: ChatRoomId,
    override val type: MessageType = MessageType.READ,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val number: ChatRoomSequence,
    override val chatRoomType: ChatRoomType
) : ChatMessage() {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatRoomSequence,
            chatRoomType: ChatRoomType
        ): ChatReadMessage {
            return ChatReadMessage(
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
                chatRoomType = chatRoomType
            )
        }
    }
}
