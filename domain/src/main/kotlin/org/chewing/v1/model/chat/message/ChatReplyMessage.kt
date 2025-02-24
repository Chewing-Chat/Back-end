package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatReplyMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val roomSequence: ChatRoomSequence,
    override val type: MessageType,
    val text: String,
    val parentMessageId: String,
    val parentMessageText: String,
    val parentSeqNumber: Int,
    val parentMessageType: ChatLogType,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            parentMessageId: String,
            parentSeqNumber: Int,
            timestamp: LocalDateTime,
            roomSequence: ChatRoomSequence,
            text: String,
            parentMessageText: String,
            type: MessageType,
            parentMessageType: ChatLogType,
            chatRoomType: ChatRoomType,
        ): ChatReplyMessage {
            return ChatReplyMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                parentMessageId = parentMessageId,
                parentSeqNumber = parentSeqNumber,
                timestamp = timestamp,
                roomSequence = roomSequence,
                text = text,
                parentMessageText = parentMessageText,
                type = type,
                parentMessageType = parentMessageType,
                chatRoomType = chatRoomType,
            )
        }
    }
}
