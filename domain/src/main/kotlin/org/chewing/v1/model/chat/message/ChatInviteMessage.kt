package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatInviteMessage private constructor(
    val messageId: String,
    override val chatRoomId: ChatRoomId,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatSequence,
    val targetUserIds: List<UserId>,
) : ChatMessage() {
    override val type: MessageType = MessageType.INVITE

    companion object {
        fun of(
            messageId: String,
            chatRoomId: ChatRoomId,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatSequence,
            targetUserIds: List<UserId>,
        ): ChatInviteMessage {
            return ChatInviteMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                timestamp = timestamp,
                number = number,
                targetUserIds = targetUserIds,
            )
        }
    }
}
