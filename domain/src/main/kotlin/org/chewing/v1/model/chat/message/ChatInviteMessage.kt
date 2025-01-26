package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatInviteMessage private constructor(
    val messageId: String,
    override val chatRoomId: String,
    override val senderId: UserId,
    override val timestamp: LocalDateTime,
    override val number: ChatLogSequence,
    val targetUserIds: List<UserId>,
) : ChatMessage() {
    override val type: MessageType = MessageType.INVITE

    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: UserId,
            timestamp: LocalDateTime,
            number: ChatLogSequence,
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
