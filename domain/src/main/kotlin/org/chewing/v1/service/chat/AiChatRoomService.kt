package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.airoom.AiChatRoomAppender
import org.chewing.v1.implementation.chat.airoom.AiChatRoomReader
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.AiChatRoomInfo
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.AiChatRoomRepository
import org.springframework.stereotype.Service

@Service
class AiChatRoomService(
    private val aiChatRoomAppender: AiChatRoomAppender,
    private val aiChatRoomReader: AiChatRoomReader,
    private val chatSequenceHandler: ChatSequenceHandler,
) {
    fun createAiChatRoom(
        userId: UserId,
    ): ChatRoomId {
        val chatRoomId = aiChatRoomAppender.appendChatRoom(userId)
        chatSequenceHandler.handleCreateRoomSequence(chatRoomId)
        chatSequenceHandler.handleCreateMemberSequences(chatRoomId, listOf(userId))
        return chatRoomId
    }
    fun getAiChatRoom(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): AiChatRoomInfo {
        return aiChatRoomReader.readRoomInfo(chatRoomId,userId)
    }

    fun increaseDirectChatRoomSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        return chatSequenceHandler.handleRoomIncreaseSequence(chatRoomId)
    }
}
