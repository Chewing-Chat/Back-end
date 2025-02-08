package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatDeleteMessage private constructor(
    val targetMessageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val number: ChatRoomSequence,
    override val type: MessageType = MessageType.DELETE,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {

    companion object {
        fun of(
            targetMessageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatRoomSequence,
            chatRoomType: ChatRoomType,
        ): ChatDeleteMessage {
            return ChatDeleteMessage(
                targetMessageId = targetMessageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
                chatRoomType = chatRoomType,
            )
        }
    }
}
