package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

sealed class ChatMessage {
    abstract val type: MessageType
    abstract val chatRoomId: String
    abstract val senderId: UserId
    abstract val timestamp: LocalDateTime
    abstract val number: ChatNumber
}
