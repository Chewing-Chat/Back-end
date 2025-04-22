package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.airoom.AiChatRoomAppender
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.AiChatRoomRepository
import org.springframework.stereotype.Service

@Service
class AiChatRoomService(
    private val aiChatRoomAppender: AiChatRoomAppender,
) {
    fun createAiChatRoom(
        userId: UserId,
    ): ChatRoomId {
        return aiChatRoomAppender.appendChatRoom(userId)
    }
}
