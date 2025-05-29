package org.chewing.v1.controller.ai

import org.chewing.v1.dto.request.chat.ChatRequest
import org.chewing.v1.dto.request.chat.ClonePromptRequest
import org.chewing.v1.dto.response.chat.AiChatMessageResponse
import org.chewing.v1.dto.response.chat.ChatRoomIdResponse
import org.chewing.v1.dto.response.chat.PairAiChatMessageResponse
import org.chewing.v1.facade.AiFacade
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class AiController(
    private val aiFacade: AiFacade,
) {
    @PostMapping("/ai/chat/room")
    fun createAiChatRoom(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<ChatRoomIdResponse> {
        val chatRoomId = aiFacade.produceAiChatRoom(userId)
        return ResponseHelper.successCreate(ChatRoomIdResponse.of(chatRoomId))
    }

    @PostMapping("/ai/chat/room/prompt")
    fun promptAiChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRequest.Common,
    ): SuccessResponseEntity<AiChatMessageResponse> {
        val prompt = aiFacade.processAiMessage(userId, request.toChatRoomId(), request.toMessage())
        return ResponseHelper.success(AiChatMessageResponse.of(prompt))
    }

    @PostMapping("/ai/chat/clone")
    fun cloneDirectChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ClonePromptRequest,
    ): SuccessResponseEntity<PairAiChatMessageResponse> {
        val (userMessage, aiMessage) = aiFacade.cloneChatAsUserFromChatRoom(
            requestingUserId = userId,
            sourceChatRoomId = ChatRoomId.of(request.sourceChatRoomId),
            targetAiChatRoomId = ChatRoomId.of(request.aiChatRoomId),
            userPrompt = request.prompt,
        )
        return ResponseHelper.success(PairAiChatMessageResponse.of(userMessage, aiMessage))
    }
}
