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
        requestingUserId: UserId,
        sourceChatRoomId: ChatRoomId,
        targetAiChatRoomId: ChatRoomId,
        userPrompt: String,
    ): Pair<ChatAiMessage, ChatAiMessage> {
        // 1. 현재 채팅방에서 상대방 ID 추출
        val directChatRoom = directChatRoomService.getDirectChatRoom(requestingUserId, sourceChatRoomId)
        val friendUserId = directChatRoom.roomInfo.friendId

        // 2. 해당 채팅방 로그 중, 상대방이 작성한 메시지만 추출
        val friendMessages = chatLogService.getChatLogsBySender(sourceChatRoomId, friendUserId)

        // 3. 사용자 입력 메시지를 AI 채팅방에 저장
        val aiChatRoom = aiChatRoomService.getAiChatRoom(targetAiChatRoomId, requestingUserId)
        val userMessageSeq = aiChatRoomService.increaseDirectChatRoomSequence(aiChatRoom.chatRoomId)
        val userMessage = chatLogService.aiMessage(aiChatRoom.chatRoomId, requestingUserId, userMessageSeq, userPrompt, ChatRoomType.AI, SenderType.USER)

        val aiMessages = chatLogService.getChatLogs(targetAiChatRoomId, userMessageSeq.sequence, 0)
        // 4. 클론용 프롬프트 생성
        val aiGeneratedPrompt = aiPromptService.promptClone(friendMessages + aiMessages, userPrompt)

        // 5. AI 응답을 실제 채팅방에 저장
        val aiResponseSeq = aiChatRoomService.increaseDirectChatRoomSequence(targetAiChatRoomId)
        val aiUserId = aiUserGenerator.getAiUserId()

        val aiMessage = chatLogService.aiMessage(sourceChatRoomId, aiUserId, aiResponseSeq, aiGeneratedPrompt, ChatRoomType.AI, SenderType.AI)

        return Pair(userMessage, aiMessage)
    }
}
