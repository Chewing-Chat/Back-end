package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId

interface AiChatRoomRepository {
    fun append(userId: UserId): ChatRoomId
}
