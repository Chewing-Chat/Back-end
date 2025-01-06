package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.media.Media
import java.time.LocalDateTime

class ChatFileLog private constructor(
    override val messageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    override val type: ChatLogType,
    val medias: List<Media>,
) : ChatLog() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: String,
            medias: List<Media>,
            timestamp: LocalDateTime,
            number: ChatNumber,
            type: ChatLogType,
        ): ChatFileLog {
            return ChatFileLog(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                medias = medias,
                timestamp = timestamp,
                number = number,
                type = type,
            )
        }
    }
}
