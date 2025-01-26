package org.chewing.v1.model.chat.log

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatFileLog private constructor(
    override val messageId: String,
    override val chatRoomId: String,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatLogSequence,
    override val type: ChatLogType,
    val medias: List<Media>,
) : ChatLog() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: UserId,
            medias: List<Media>,
            timestamp: LocalDateTime,
            number: ChatLogSequence,
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
