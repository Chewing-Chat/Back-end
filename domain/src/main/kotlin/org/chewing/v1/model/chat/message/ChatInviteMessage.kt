package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatInviteMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    val roomSequence: ChatRoomSequence,
    val targetUserIds: List<UserId>,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {
    override val type: MessageType = MessageType.INVITE

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            timestamp: LocalDateTime,
            roomSequence: ChatRoomSequence,
            targetUserIds: List<UserId>,
            chatRoomType: ChatRoomType,
        ): ChatInviteMessage {
            return ChatInviteMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                roomSequence = roomSequence,
                targetUserIds = targetUserIds,
                chatRoomType = chatRoomType,

            )
        }
    }
}
