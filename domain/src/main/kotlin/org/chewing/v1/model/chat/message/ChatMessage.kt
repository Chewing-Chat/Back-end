package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

sealed class ChatMessage {
    abstract val type: MessageType
    abstract val chatRoomId: String
    abstract val senderId: String
    abstract val timestamp: LocalDateTime
    abstract val number: ChatNumber
}
