package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatLeaveMessage private constructor(
    val messageId: String,
    override val chatRoomId: String,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val type: MessageType = MessageType.LEAVE,
    override val number: ChatLogSequence,
) : ChatMessage() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatLogSequence,
        ): ChatLeaveMessage {
            return ChatLeaveMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
            )
        }
    }
}
