package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

sealed class ChatLog {
    abstract val messageId: String
    abstract val type: ChatLogType
    abstract val chatRoomId: ChatRoomId
    abstract val senderId: UserId
    abstract val timestamp: LocalDateTime
    abstract val number: ChatSequence
}
