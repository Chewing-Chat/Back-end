package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

sealed class ChatLog {
    abstract val messageId: String
    abstract val type: ChatLogType
    abstract val chatRoomId: String
    abstract val senderId: UserId
    abstract val timestamp: LocalDateTime
    abstract val number: ChatLogSequence
}
