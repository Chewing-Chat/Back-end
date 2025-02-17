package org.chewing.v1.dto.request.chat

import org.chewing.v1.model.chat.room.ChatRoomId

class ChatRequest {
    data class Reply(
        val chatRoomId: String,
        val parentMessageId: String,
        val message: String,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toParentMessageId(): String {
            return parentMessageId
        }
    }
    data class Read(
        val chatRoomId: String,
        val sequenceNumber: Int,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toSequenceNumber(): Int {
            return sequenceNumber
        }
    }
    data class Common(
        val chatRoomId: String,
        val message: String,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toMessage(): String {
            return message
        }
    }
    data class Delete(
        val chatRoomId: String,
        val messageId: String,
    ) {
        fun toChatRoomId(): ChatRoomId {
            return ChatRoomId.of(chatRoomId)
        }
        fun toMessageId(): String {
            return messageId
        }
    }
}
