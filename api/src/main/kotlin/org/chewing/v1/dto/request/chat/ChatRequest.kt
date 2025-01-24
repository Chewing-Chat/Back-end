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
    data class Bomb(
        val chatRoomId: String,
        val message: String,
        val expiredAt: String,
    ) {
        fun toExpireAt(): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
            return LocalDateTime.parse(expiredAt, formatter)
        }
    }
}
