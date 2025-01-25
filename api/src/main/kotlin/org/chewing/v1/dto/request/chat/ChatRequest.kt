package org.chewing.v1.dto.request.chat

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatRequest {
    data class Reply(
        val chatRoomId: String,
        val parentMessageId: String,
        val message: String,
    )
    data class Read(
        val chatRoomId: String,
    )
    data class Common(
        val chatRoomId: String,
        val message: String,
    )
    data class Delete(
        val chatRoomId: String,
        val messageId: String,
    )

}
