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
import org.chewing.v1.service.chat.DirectChatRoomService
import org.springframework.stereotype.Service

@Service
class AiFacade(
    private val aiChatRoomService: AiChatRoomService,
    private val aiPromptService: AiPromptService,
    private val chatLogService: ChatLogService,
    private val aiUserGenerator: AiUserGenerator,
    private val directChatRoomService: DirectChatRoomService,
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
        return aiChatRoomService.createAiChatRoom(userId)
    }

    fun cloneChatAsUserFromChatRoom(
        requester: UserId,
        chatRoomId: ChatRoomId,
        prompt: String,
    ): ChatAiMessage {
        // 1. 현재 채팅방에서 상대방 ID 추출
        val directChatRoom = directChatRoomService.getDirectChatRoom(requester, chatRoomId)
        val targetUserId = directChatRoom.roomInfo.friendId

        // 2. 해당 채팅방 로그 중, targetUserId가 작성한 메시지만 추출
        val targetLogs = chatLogService.getChatLogsBySender(chatRoomId, targetUserId)

        // 3. 프롬프트 생성
        val clonePrompt = aiPromptService.promptClone(targetLogs, prompt)

        // 4. AI 응답을 현재 채팅방에 저장
        val sequence = aiChatRoomService.increaseDirectChatRoomSequence(chatRoomId)
        val aiUserId = aiUserGenerator.getAiUserId()

        return chatLogService.aiMessage(chatRoomId, aiUserId, sequence, clonePrompt, ChatRoomType.DIRECT, SenderType.AI)
    }


}
