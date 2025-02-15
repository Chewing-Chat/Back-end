package org.chewing.v1.model.chat.message

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class ChatErrorMessage private constructor(
    override val chatRoomId: ChatRoomId,
    val errorCode: ErrorCode,
    override val timestamp: LocalDateTime = LocalDateTime.now(),
    override val type: MessageType = MessageType.ERROR,
    override val senderId: UserId,
    override val chatRoomType: ChatRoomType,
) : ChatMessage() {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            errorCode: ErrorCode,
            userId: UserId,
            chatRoomType: ChatRoomType,
        ): ChatErrorMessage {
            return ChatErrorMessage(
                chatRoomId = chatRoomId,
                errorCode = errorCode,
                senderId = userId,
                chatRoomType = chatRoomType,
            )
        }
    }
}
