package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatFileMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val number: ChatRoomSequence,
    override val type: MessageType = MessageType.FILE,
    val medias: List<Media>,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            medias: List<Media>,
            timestamp: LocalDateTime,
            number: ChatRoomSequence,
            chatRoomType: ChatRoomType,
        ): ChatFileMessage {
            return ChatFileMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                medias = medias,
                timestamp = timestamp,
                number = number,
                chatRoomType = chatRoomType,
            )
        }
    }
}
