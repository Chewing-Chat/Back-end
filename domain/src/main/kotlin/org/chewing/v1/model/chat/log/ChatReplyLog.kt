package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatReplyLog private constructor(
    override val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatSequence,
    override val type: ChatLogType,
    val text: String,
    val parentMessageId: String,
    val parentMessageText: String,
    val parentSeqNumber: Int,
    val parentMessageType: ChatLogType,
) : ChatLog() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            parentMessageId: String,
            parentSeqNumber: Int,
            timestamp: LocalDateTime,
            number: ChatSequence,
            text: String,
            parentMessageText: String,
            type: ChatLogType,
            parentMessageType: ChatLogType,
        ): ChatReplyLog {
            return ChatReplyLog(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                parentMessageId = parentMessageId,
                parentSeqNumber = parentSeqNumber,
                timestamp = timestamp,
                number = number,
                text = text,
                parentMessageText = parentMessageText,
                type = type,
                parentMessageType = parentMessageType,
            )
        }
    }
}
