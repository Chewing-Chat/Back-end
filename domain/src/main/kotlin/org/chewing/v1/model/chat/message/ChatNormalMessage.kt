package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatNormalMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatSequence,
    val text: String,
) : ChatMessage() {
    override val type: MessageType = MessageType.NORMAL

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            text: String,
            number: ChatSequence,
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
