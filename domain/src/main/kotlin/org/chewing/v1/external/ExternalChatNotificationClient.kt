package org.chewing.v1.external

import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId

interface ExternalChatNotificationClient {
    fun sendMessage(chatMessage: ChatMessage, userId: UserId)
    fun sendOwnedMessage(chatMessage: ChatMessage)
}
