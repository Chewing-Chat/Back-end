package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.log.ChatLog

data class ChatLogsResponse(
    val chatLogs: List<ChatLogResponse>,
) {
    companion object {
        fun from(list: List<ChatLog>): ChatLogsResponse {
            return ChatLogsResponse(
                chatLogs = list.map { ChatLogResponse.from(it) },
            )
        }
    }
}
