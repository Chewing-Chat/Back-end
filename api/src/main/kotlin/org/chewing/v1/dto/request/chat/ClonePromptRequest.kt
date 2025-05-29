package org.chewing.v1.dto.request.chat

data class ClonePromptRequest(
    val prompt: String,
    val sourceChatRoomId: String,
    val aiChatRoomId: String,
)
