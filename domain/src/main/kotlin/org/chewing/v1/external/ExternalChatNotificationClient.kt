package org.chewing.v1.external

import org.chewing.v1.model.chat.message.ChatMessage

interface ExternalChatNotificationClient {
    fun sendMessage(chatMessage: ChatMessage, userId: String)
}
