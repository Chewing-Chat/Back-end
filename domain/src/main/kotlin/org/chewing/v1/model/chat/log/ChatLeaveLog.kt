package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatLeaveLog private constructor(
    override val messageId: String,
    override val chatRoomId: String,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatLogSequence,
    override val type: ChatLogType,
) : ChatLog() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatLogSequence,
            type: ChatLogType,
        ): ChatLeaveLog {
            return ChatLeaveLog(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
                type = type,
            )
        }
    }
}
