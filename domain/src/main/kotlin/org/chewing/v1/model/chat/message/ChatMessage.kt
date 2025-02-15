package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

sealed class ChatMessage {
    abstract val type: MessageType
    abstract val chatRoomId: ChatRoomId
    abstract val senderId: UserId
    abstract val timestamp: LocalDateTime
    abstract val chatRoomType: ChatRoomType
}
