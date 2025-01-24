package org.chewing.v1.dto.request.ai

class AiRequest {
    data class ChatSearch(
        val chatRoomId: String,
        val prompt: String,
    )

    data class Schedule(
        val prompt: String,
    )
}
