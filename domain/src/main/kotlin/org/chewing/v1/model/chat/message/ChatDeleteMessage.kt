package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatDeleteMessage private constructor(
    val targetMessageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatSequence,
    override val type: MessageType = MessageType.DELETE,
) : ChatMessage() {

    companion object {
        fun of(
            targetMessageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatSequence,
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
