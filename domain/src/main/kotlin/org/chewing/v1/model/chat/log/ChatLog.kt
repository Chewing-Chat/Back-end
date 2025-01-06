package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

sealed class ChatLog {
    abstract val messageId: String
    abstract val type: ChatLogType
    abstract val chatRoomId: String
    abstract val senderId: String
    abstract val timestamp: LocalDateTime
    abstract val number: ChatNumber
}
