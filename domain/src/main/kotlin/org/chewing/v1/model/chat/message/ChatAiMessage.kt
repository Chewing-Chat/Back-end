package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatAiMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val roomSequence: ChatRoomSequence,
    val text: String,
    val senderType: SenderType,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {
    override val type: MessageType = MessageType.NORMAL

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            text: String,
            roomSequence: ChatRoomSequence,
            timestamp: LocalDateTime,
            chatRoomType: ChatRoomType,
            senderType: SenderType,
        ): ChatAiMessage {
            return ChatAiMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                text = text,
                roomSequence = roomSequence,
                timestamp = timestamp,
                chatRoomType = chatRoomType,
                senderType = senderType,
            )
        }
    }
}
