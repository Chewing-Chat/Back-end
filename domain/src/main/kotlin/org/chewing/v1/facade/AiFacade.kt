package org.chewing.v1.facade

import org.chewing.v1.implementation.ai.AiUserGenerator
import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.message.ChatAiMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.ai.AiPromptService
import org.chewing.v1.service.chat.AiChatRoomService
import org.chewing.v1.service.chat.ChatLogService
import org.springframework.stereotype.Service

@Service
class AiFacade(
    private val aiChatRoomService: AiChatRoomService,
    private val aiPromptService: AiPromptService,
    private val chatLogService: ChatLogService,
    private val aiUserGenerator: AiUserGenerator
) {
    fun processAiMessage(
        userId: UserId,
        chatRoomId: ChatRoomId,
        text: String,
    ): ChatAiMessage {
        val aiChatRoom = aiChatRoomService.getAiChatRoom(chatRoomId, userId)
        val chatSequence = aiChatRoomService.increaseDirectChatRoomSequence(aiChatRoom.chatRoomId)
        chatLogService.aiMessage(aiChatRoom.chatRoomId, userId, chatSequence, text, ChatRoomType.AI, SenderType.USER)
        val chatLogs = chatLogService.getChatLogs(aiChatRoom.chatRoomId, chatSequence.sequence, 0)
        val aiPromptMessage = aiPromptService.prompt(chatLogs)
        val aiUserId = aiUserGenerator.getAiUserId()
        val aiChatMessage = chatLogService.aiMessage(aiChatRoom.chatRoomId, aiUserId, chatSequence, aiPromptMessage, ChatRoomType.AI, SenderType.AI)
        return aiChatMessage
    }

    fun produceAiChatRoom(
        userId: UserId,
    ): ChatRoomId {
        return  aiChatRoomService.createAiChatRoom(userId)
    }
}
