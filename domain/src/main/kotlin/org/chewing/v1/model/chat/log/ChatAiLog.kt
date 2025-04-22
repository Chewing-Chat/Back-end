package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatAiLog private constructor(
    override val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val roomSequence: ChatRoomSequence,
    override val type: ChatLogType,
    val text: String,
    val senderType: SenderType
) : ChatLog() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            text: String,
            roomSequence: ChatRoomSequence,
            timestamp: LocalDateTime,
            type: ChatLogType,
            senderType: SenderType
        ): ChatAiLog {
            return ChatAiLog(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                text = text,
                roomSequence = roomSequence,
                timestamp = timestamp,
                type = type,
                senderType = senderType
            )
        }
    }
}
