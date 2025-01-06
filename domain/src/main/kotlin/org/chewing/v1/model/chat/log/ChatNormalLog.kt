package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

class ChatNormalLog private constructor(
    override val messageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    override val type: ChatLogType,
    val text: String,
) : ChatLog() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: String,
            text: String,
            number: ChatNumber,
            timestamp: LocalDateTime,
            type: ChatLogType,
        ): ChatNormalLog {
            return ChatNormalLog(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                text = text,
                number = number,
                timestamp = timestamp,
                type = type,
            )
        }
    }
}
