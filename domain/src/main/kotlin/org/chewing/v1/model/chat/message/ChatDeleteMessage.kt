package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatDeleteMessage private constructor(
    val targetMessageId: String,
    override val chatRoomId: String,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatLogSequence,
    override val type: MessageType = MessageType.DELETE,
) : ChatMessage() {

    companion object {
        fun of(
            targetMessageId: String,
            chatRoomId: String,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatLogSequence,
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
