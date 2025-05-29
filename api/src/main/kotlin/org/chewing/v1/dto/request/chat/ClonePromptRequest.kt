package org.chewing.v1.dto.request.chat

import org.chewing.v1.model.chat.room.ChatRoomId

data class ClonePromptRequest(
    val prompt: String,
    val sourceChatRoomId: String,
    val aiChatRoomId: String,
)
