package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.media.Media
import java.time.LocalDateTime

class ChatFileMessage private constructor(
    val messageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    override val type: MessageType = MessageType.FILE,
    val medias: List<Media>,
) : ChatMessage() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: String,
            medias: List<Media>,
            timestamp: LocalDateTime,
            number: ChatNumber,
        ): ChatFileMessage {
            return ChatFileMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                medias = medias,
                timestamp = timestamp,
                number = number,
            )
        }
    }
}
