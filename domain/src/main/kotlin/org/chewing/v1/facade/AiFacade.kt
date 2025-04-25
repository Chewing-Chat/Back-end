package org.chewing.v1.facade

import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
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
) {
    fun processAiMessage(
        userId: UserId,
        chatRoomId: ChatRoomId,
        text: String,
    ): String {
        val directChatRoom = aiChatRoomService.getAiChatRoom(chatRoomId, userId)
        val chatSequence = aiChatRoomService.increaseDirectChatRoomSequence(directChatRoom.chatRoomId)
        chatLogService.aiMessage(directChatRoom.chatRoomId, userId, chatSequence, text, ChatRoomType.AI, SenderType.USER)
        val chatLogs = chatLogService.getChatLogs(directChatRoom.chatRoomId, chatSequence.sequence, 0)
        val aiMessage = aiPromptService.prompt(chatLogs)
        chatLogService.aiMessage(directChatRoom.chatRoomId, userId, chatSequence, aiMessage, ChatRoomType.AI, SenderType.AI)
        return aiMessage
    }
}
