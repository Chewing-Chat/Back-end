package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.log.ChatLog

data class ChatLogResponse(
    val chatLogs: List<ChatLogMessageResponse>,
) {
    companion object {
        fun from(list: List<ChatLog>): ChatLogResponse {
            return ChatLogResponse(
                chatLogs = list.map { ChatLogMessageResponse.from(it) },
            )
        }
    }
}
