package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatNormalLog private constructor(
    override val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatSequence,
    override val type: ChatLogType,
    val text: String,
) : ChatLog() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            text: String,
            number: ChatSequence,
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
